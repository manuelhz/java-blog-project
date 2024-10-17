package org.bytebounty.app.security;

import java.util.Optional;

import org.bytebounty.app.model.Account;
import org.bytebounty.app.model.Comment;
import org.bytebounty.app.model.Post;
import org.bytebounty.app.service.AccountService;
import org.bytebounty.app.service.CommentService;
import org.bytebounty.app.service.PostService;
import org.bytebounty.app.service.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {
    
    @Autowired
    PostService postService;

    
    Authentication authentication;

    @Autowired
    CommentService commentService;


    

    @Autowired
    AccountService accountService;

    @Autowired
    PrivilegeService privilegeService; 

    public boolean isWriter(Long id) {
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Post> optionalPost = postService.findById(id);
        if (!optionalPost.isPresent()) {
            return false;
        }
        Post post = optionalPost.get();
        String user = post.getAccount().getEmail();
        return authentication.getName().equals(user);
    }

    // check if the user has any of the privileges of the argument of the method
    public boolean hasPrivilege(String... privileges) {
        this.authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Account> optionalAccount = accountService.findOneByEmail(authentication.getName());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            for (String privilege: privileges) {
                if (account.hasPrivilege(privilegeService.findByName(privilege))) {
                    return true;
                }
            }
            return false;            
        } else {
            return false;
        }
    }

    public boolean isCommenter(Long commentId) {

        // get authenticated user
        this.authentication = SecurityContextHolder.getContext().getAuthentication();

        // get the comment
        Optional<Comment> optionalComment = commentService.findById(commentId);
        if (!optionalComment.isPresent()) {
            return false;
        }        
        Comment comment = optionalComment.get();

        // get the user that is trying to comment
        String user = comment.getPostAccount().getEmail();

        // verifying if who make the comment is the same as the logged user
        return authentication.getName().equals(user);

    }

}