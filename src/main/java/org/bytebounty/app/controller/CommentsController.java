package org.bytebounty.app.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.bytebounty.app.model.Account;
import org.bytebounty.app.model.Comment;
import org.bytebounty.app.model.Post;
import org.bytebounty.app.model.Role;
import org.bytebounty.app.service.AccountService;
import org.bytebounty.app.service.CommentService;
import org.bytebounty.app.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CommentsController {

    @Autowired
    AccountService accountService;

    @Autowired
    RoleService roleService;

    @Autowired
    CommentService commentService;

    // anyone can comment
    @PostMapping("/blog/comments/add")
    public String addComment(
        @ModelAttribute Comment comment, 
        Principal principal
        ) {
        Optional<Account> optionalAccount = null;
        
        if (principal != null) {
            // if user is logged in then find them
            optionalAccount = accountService.findOneByEmail(principal.getName());
        } else {
            // but if user is not logged in find the account with the email typed by the user            
            optionalAccount = accountService.findOneByEmail(comment.getPostAccount().getEmail());
            Account account = optionalAccount.get();
            
            // however if the account is associated to a password the user needs to login
            if (account.getPassword() != null) {
                return "redirect:/login";
            }   
        }

        // if at this point optionalAccount is not pressent is because it is a new user and needs to be created
        // though without a password and after that save the comment
        if (!optionalAccount.isPresent()){
            Account account = comment.getPostAccount();
            // accounts need to have roles
            account.setRoles(new HashSet<Role>(Arrays.asList(roleService.findByName("ROLE_SUBSCRIBER"))));
            accountService.save(comment.getPostAccount());
        } else {
            
            comment.setPostAccount(optionalAccount.get());
        }

        commentService.save(comment);

        return "redirect:/blog/posts/" + comment.getParentPost().getSlug() + "#comments";
    }

    // Delete comment
    @GetMapping("/blog/comments/{commentId}/delete")
    @PreAuthorize("@securityService.isCommenter(#commentId) or @securityService.hasPrivilege('MODERATE_COMMENTS')")
    public String deleteComment(@PathVariable Long commentId) {

        Optional<Comment> optionalComment = commentService.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            Post post;
            if (comment.getParentPost() != null) {
                post = comment.getParentPost();
            } else {
                post = comment.getParentComment().getParentPost();
            }             
            commentService.delete(comment);
            return "redirect:/blog/posts/" + post.getSlug() + "#comments";
        } else {
            return "404";
        }

    }

    // Edidt comment
    @PostMapping(path = "/blog/comments/{commentId}/edit")
    @PreAuthorize("@securityService.isCommenter(#commentId) or @securityService.hasPrivilege('MODERATE_COMMENTS')")
    public String editComment(
        @PathVariable("commentId") Long commentId, 
        @ModelAttribute("comment") Comment editedComment
        ) {

        Optional<Comment> optionalComment = commentService.findById(commentId);
        if (optionalComment.isPresent()) {
            String slug;
            Comment comment = optionalComment.get();
            comment.setContent(editedComment.getContent());
            if (comment.getParentPost() != null) {
                slug = comment.getParentPost().getSlug();
            } else {
                slug = comment.getParentComment().getParentPost().getSlug();
            }
            comment.setContent(editedComment.getContent());
            commentService.save(comment);
            return "redirect:/blog/posts/" + slug + "#comments";
        }
        return null;
        
    }

    @PostMapping("/blog/comments/{commentId}/answer")
    public String replyComment(
        @ModelAttribute("reply") Comment comment, 
        @PathVariable("commentId") Long commentId, 
        Principal principal) {
        Optional<Account> optionalAccount;

        if (principal != null) {
            optionalAccount = accountService.findOneByEmail(principal.getName());
        } else {
            optionalAccount = accountService.findOneByEmail(comment.getPostAccount().getEmail());
        }
        
        if (!optionalAccount.isPresent()) {
            accountService.save(comment.getPostAccount());
        } else {
            comment.setPostAccount(optionalAccount.get());
        }
        Optional<Comment> optionalParentComment = commentService.findById(commentId);
        if (optionalParentComment.isPresent()) {
            comment.setParentComment(optionalParentComment.get());
            commentService.save(comment);
            return "redirect:/blog/posts/" + comment.getParentComment().getParentPost().getSlug() + "#comments";
            
        } else {
            return "404";
        }
              

        
    }
    
}
