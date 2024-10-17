package org.bytebounty.app.controller;




import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bytebounty.app.model.Account;
import org.bytebounty.app.model.Category;
import org.bytebounty.app.model.Comment;
import org.bytebounty.app.model.Post;
import org.bytebounty.app.model.Tag;
import org.bytebounty.app.repository.PrivilegeRepository;
import org.bytebounty.app.security.SecurityService;
import org.bytebounty.app.service.PostService;
import org.bytebounty.app.service.RoleService;
import org.bytebounty.app.service.TagService;

import org.bytebounty.app.util.SortCommentByDate;
import org.bytebounty.app.util.FileStorage;
import org.bytebounty.app.service.AccountService;
import org.bytebounty.app.service.CategoryService;
import org.bytebounty.app.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Controller
public class BlogController {

    @Autowired
    PostService postService;

    @Autowired
    CommentService commentService;

    @Autowired
    AccountService accountService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    TagService tagService;

    @Autowired
    SecurityService securityService;

    @Autowired
    RoleService roleService;

    @Autowired
    PrivilegeRepository privilegeRepository;

    // show all posts
    @GetMapping("/blog/posts/")
    public String blogGrid(
        Model model,
        @RequestParam(name = "page", defaultValue = "1", required = false) String page,
        @RequestParam(name = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
        @RequestParam(name = "perPage", defaultValue = "6", required = false) String perPage,
        Principal principal
    ) {

        // only if user is editor can see posts that haven't been published
        boolean editor;
        if (principal != null) {
            String name = principal.getName();
            Account currentAccount = accountService.findOneByEmail(name).get();
            if (currentAccount.hasPrivilege(privilegeRepository.findByName("PUBLISH_POSTS"))) {
                editor = true;
            } else {
                editor = false;
            }
        } else {
            editor = false;
        }        

        // number or index of the current page to be displayd
        int currentPage = Integer.parseInt(page);
        model.addAttribute("page", currentPage);

        // number of post per page
        int postPerPage = Integer.parseInt(perPage);

        // the page of posts that will be sent to the brownser
        Page<Post> posts;

        if (editor) {
            // only if user is editor can see posts that haven't been published
            posts = postService.findAll((currentPage-1), postPerPage, sortBy);
        } else {
            Pageable paging = PageRequest.of((currentPage-1), postPerPage, Sort.by(sortBy).descending());
            posts = postService.findByPublishedTrue(paging);
        }        
        
        model.addAttribute("posts", posts);

        // add totalPages to the model
        int totalPages = posts.getTotalPages();
        model.addAttribute("totalPages", totalPages);

        // the last three posts to the model to be shown at the top of the blog
        List<Post> lastThreePosts = postService.findLastThreePublishedPosts();
        model.addAttribute("lastThreePosts", lastThreePosts);

        return"blogView/blogGrid";
    }


    // show post detail by slug
    @GetMapping({"/blog/posts/{slug}", "/blog/posts/{slug}/{arrow}"})
    public String blogDetailBySlug(
        @PathVariable String slug,
        @PathVariable Optional<String> arrow,
        Model model) {
        
        Optional<Post> optionalPost = null;

        if(!arrow.isPresent()) {
            optionalPost = postService.findBySlug(slug);
        } else {
            String direction = arrow.get();
            if(direction.equals("next")){
                Post nextPost = postService.findNextById(postService.findBySlug(slug).get().getPostId()).get() ;
                return "redirect:/blog/posts/" + nextPost.getSlug();
            } else if (direction.equals("previous")){
                Post previousPost = postService.findPreviousById(postService.findBySlug(slug).get().getPostId()).get();

                return "redirect:/blog/posts/" +  previousPost.getSlug();
            } else {
                return "404";
            }
        }

        if (optionalPost.isPresent()) {            

            // find post
            Post post = optionalPost.get();
            model.addAttribute("post", post);

            // find last three posts
            List<Post> lastThreePosts = postService.findLastThreePublishedPosts();
            model.addAttribute("lastThreePosts", lastThreePosts);

            // isLast and isFirst are swaped if we are reading the posts from the newest to the oldest
            Boolean isLastPost = postService.isFirstPost(post.getPostId());
            Boolean isFirstPost = postService.isLastPost(post.getPostId());

            model.addAttribute("isLastPost", isLastPost);
            model.addAttribute("isFirstPost", isFirstPost);

            // Create an empty comment
            Comment comment = new Comment();
            // comment.setPostAccount(new Account());
            comment.setParentPost(post);
            model.addAttribute("comment", comment);

            // add the comparator
            model.addAttribute("commentComparator", new SortCommentByDate());

            // add an answer comment
            Comment reply = new Comment();
            reply.setParentComment(new Comment());
            model.addAttribute("reply", reply);

            return "blogView/blogDetail";

        } else {

            return "404";
        }
    }

    // edit post
    @GetMapping("/blog/posts/{postId}/edit")
    @PreAuthorize(
        "@securityService.hasPrivilege('EDIT_OTHERS_POSTS') or" +
        "(@securityService.hasPrivilege('EDIT_POSTS') and" +
        "@securityService.isWriter(#postId))")
    public String editPost(@PathVariable Long postId, Model model) {

        Optional<Post> optionalPost = postService.findById(postId);

        if (optionalPost.isPresent()) {


            Post post = optionalPost.get();



            // add the post to the model
            model.addAttribute("post", post);
            // retrieving all categories for the select to show them
            List<Category> allCategories = categoryService.findAll();
            model.addAttribute("allCategories", allCategories);

            // retrive all tags for the select to display
            List<Tag> allTags = tagService.findAll();
            model.addAttribute("allTags", allTags);

            // retriving all Posts to show them in the select as a potential parent post
            List<Post> allPosts = postService.findAll();
            model.addAttribute("allPosts", allPosts);

            List<String> oldPostImagePaths = FileStorage.imageTagExtractor(post.getContent());
            model.addAttribute("oldPostImagePaths", oldPostImagePaths);

            return "blogView/blogEdit";

        } else {
            return "404";
        }
    }
    // edit post processing
    @PostMapping("/blog/posts/edit")
    @PreAuthorize(
        "@securityService.hasPrivilege('EDIT_OTHERS_POSTS') or" +
        "@securityService.hasPrivilege('EDIT_POSTS') or" +
        "@securityService.isWriter(#postId)")
    public String editPostProcessing(
        @RequestParam("file") MultipartFile file,
        @RequestParam("oldPostImagePaths") List<String> oldPostImagePaths,
        @Valid @ModelAttribute Post post,
        BindingResult result,
        Model model,
        Principal principal
    ) {

        if(result.hasErrors()) {
            // retrieving all categories for the select to show them
            List<Category> allCategories = categoryService.findAll();
            model.addAttribute("allCategories", allCategories);

            // retrive all tags for the select to display
            List<Tag> allTags = tagService.findAll();
            model.addAttribute("allTags", allTags);

            // retriving all Posts to show them in the select as a potential parent post
            List<Post> allPosts = postService.findAll();
            model.addAttribute("allPosts", allPosts);

            model.addAttribute("oldPostImagePaths", oldPostImagePaths);
            return "blogView/blogEdit";
        }

        Optional<Post> optionalPost = postService.findById(post.getPostId());
        Post editedPost = optionalPost.get();

        if(optionalPost.isPresent()) {
            if(!file.isEmpty()) {
                // delete delete previous image from storage

                FileStorage.delete(editedPost.getFeaturedImage());
                editedPost.setFeaturedImage(FileStorage.save(file));
            }

            // clean posts img folder:
            List<String> newPostImagePaths = FileStorage.cleanPostsImg(oldPostImagePaths, post);                

            // empty tmp folder and post.content field
            post = FileStorage.emptyTmp(post, newPostImagePaths);

            editedPost.setTitle(post.getTitle());
            editedPost.setImageCredit(post.getImageCredit());
            editedPost.setMetaSummary(post.getMetaSummary());
            editedPost.setContent(post.getContent());
            editedPost.setCategories(post.getCategories());            
            editedPost.setTags(post.getTags());            
            editedPost.setParentPost(post.getParentPost());            
            editedPost.setUpdatedAt(LocalDateTime.now());
            Optional<Account> optionalAccount = accountService.findOneByEmail(principal.getName());
            if (optionalAccount.isPresent()) {
                Account account = optionalAccount.get();
                if ( account.hasPrivilege(privilegeRepository.findByName("PUBLISH_POSTS"))) {
                    editedPost.setPublished(post.isPublished());
                }
            }            
            editedPost.setEnableComments(post.isEnableComments());
            editedPost.setEnableNewComments(post.isEnableNewComments());
            postService.save(editedPost);
            FileStorage.waitt();

            return "redirect:/blog/posts/" + editedPost.getSlug();          
            
        } else {
            return "404";
        }
    }

    // delete post
    @GetMapping("/blog/posts/{postId}/delete")
    @PreAuthorize(
        "(@securityService.hasPrivilege('DELETE_POSTS') and " +
        "@securityService.isWriter(#postId) ) "+
        "or @securityService.hasPrivilege('DELETE_OTHERS_POSTS')")
    public String delerePost(
        @PathVariable Long postId,
        Model model        
    ) {

        Optional<Post> optionalPost = postService.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            

            postService.delete(post);
            return "redirect:/blog/posts/";

        } else {
            return "404";
        }
    }



    @GetMapping("/blog/posts/create")
    @PreAuthorize("@securityService.hasPrivilege('PUBLISH_POSTS')")
    public String createBlog(Model model) {

        // add a post object to the model
        Post post = new Post();
        post.setPublished(false);
        post.setEnableComments(true);
        post.setEnableNewComments(true);
        model.addAttribute("post", post);

        // adding all categories
        List<Category> categories = categoryService.findAll();
        model.addAttribute("allCategories", categories);

        // add all Tags
        List<Tag> tags = tagService.findAll();
        model.addAttribute("allTags", tags);

        // all Posts TODO: it needs to send only pages with ajax
        List<Post> posts = postService.findAll();
        model.addAttribute("allPosts", posts);



        return "blogView/blogCreate";

    }

    @PostMapping("/blog/posts/create")
    @PreAuthorize("@securityService.hasPrivilege('PUBLISH_POSTS')")
    public String createBlogPersist(
        @RequestParam("file") MultipartFile file, 
        @Valid @ModelAttribute Post post,
        BindingResult result,
        Principal principal,
        Model model
        ) {
            if (result.hasErrors()) {
                // adding all categories
                List<Category> categories = categoryService.findAll();
                model.addAttribute("allCategories", categories);

                // add all Tags
                List<Tag> tags = tagService.findAll();
                model.addAttribute("allTags", tags);

                // all Posts TODO: it needs to send only pages with ajax
                List<Post> posts = postService.findAll();
                model.addAttribute("allPosts", posts);

                model.addAttribute("file", file);
                return "blogView/blogCreate";
            }
            if (principal != null) {
                if(!file.isEmpty()){
                    post.setFeaturedImage(FileStorage.save(file));            
                }
                String authUser = principal.getName();                
                Account account = accountService.findOneByEmail(authUser).get();
                post.setAccount(account);


                // empty tmp folder and edit post.content field
                post = FileStorage.emptyTmp(post);

                // wait for the storage to update
                FileStorage.waitt();

                postService.save(post);
                return "redirect:/blog/posts/" + post.getSlug();

            } else {
                return "/?error";
            }
        
        
        
    }

    


}
