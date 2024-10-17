package org.bytebounty.app.controller;

import java.util.List;
import java.util.Optional;

import org.bytebounty.app.model.Post;
import org.bytebounty.app.model.Tag;
import org.bytebounty.app.service.PostService;
import org.bytebounty.app.service.TagService;
import org.bytebounty.app.util.FileStorage;
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
public class TagController {

    @Autowired
    TagService tagService;

    @Autowired
    PostService postService;

    // show Tags:
    @GetMapping("/blog/tags")
    public String tags(Model model) {

        List<Tag> tags = tagService.findAll();

        model.addAttribute("tags", tags);

        return "tagView/tags";
    }

    // edit tag
    @GetMapping("/blog/tags/{tagId}/edit")
    @PreAuthorize("@securityService.hasPrivilege('MANAGE_CATEGORIES')")
    public String editTag(@PathVariable Long tagId, Model model) {
        Optional<Tag> optionalTag = tagService.findById(tagId);

        if (optionalTag.isPresent()) {
            Tag tag = optionalTag.get();

            model.addAttribute("tag", tag);
            return "tagView/tagEdit";
        } else {
            return "404";
        }
    }
    @PostMapping("/blog/tags/edit")
    @PreAuthorize("@securityService.hasPrivilege('MANAGE_CATEGORIES')")
    public String editTagPost(
        @RequestParam("file") MultipartFile file,
        @Valid @ModelAttribute Tag tagEdited,
        BindingResult result
    ) {
        if (result.hasErrors()) {

            return "tagView/tagEdit";

        }
        
        Optional<Tag> optionalTag = tagService.findById(tagEdited.getTagId());
        if (optionalTag.isPresent()) {
            Tag tag = optionalTag.get();
            tag.setSlug(tagEdited.getSlug());
            tag.setTagDescription(tagEdited.getTagDescription());
            tag.setTagName(tagEdited.getTagName());
            tag.setImageCredit(tagEdited.getImageCredit());
            if (!file.isEmpty()) {
                FileStorage.delete(tag.getThumbnail());
                tag.setThumbnail(FileStorage.save(file, "tags"));
                FileStorage.waitt();
            }
            tagService.save(tag);

            return "redirect:/blog/tags";

        } else {
            return "404";
        }
        
    }

    // Create Tag
    @GetMapping("/blog/tags/create")
    @PreAuthorize("@securityService.hasPrivilege('MANAGE_CATEGORIES')")
    public String createTag(Model model) {
        Tag tag = new Tag();
        model.addAttribute("tag", tag);

        return "tagView/tagCreate";
    }
    @PostMapping("/blog/tags/create")
    @PreAuthorize("@securityService.hasPrivilege('MANAGE_CATEGORIES')")
    public String createTagPost(
        @RequestParam("file") MultipartFile file,
        @Valid @ModelAttribute Tag newTag,
        BindingResult rersult
    ) {
        if (rersult.hasErrors()) {
            return "tagView/tagCreate";
        }
        if(!file.isEmpty()) {
            newTag.setThumbnail(FileStorage.save(file, "tags"));
            FileStorage.waitt();            
        }
        tagService.save(newTag);
        return "redirect:/blog/tags";
    }

    // delete Tag
    @GetMapping("/blog/tags/{tagId}/delete")
    @PreAuthorize("@securityService.hasPrivilege('MANAGE_CATEGORIES')")
    public String deleteTag(@PathVariable("tagId") Long tagId, Model model) {

        Optional<Tag> optionalTag = tagService.findById(tagId);
        if (optionalTag.isPresent()){
            Tag tag = optionalTag.get();
            tagService.delete(tag);

            return "redirect:/blog/tags";
        } else {
            return "404";
        }        

    }
    // show tag and belonging posts
    @GetMapping("blog/tags/{slug}")
    public String tag(
        @PathVariable("slug") String slug, 
        Model model,
        @RequestParam(name = "page", defaultValue = "1", required = false) String page,
        @RequestParam(name = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
        @RequestParam(name = "perPage", defaultValue = "4", required = false) String perPage        
        ) {

        Optional<Tag> optionaltag = tagService.findBySlug(slug);

        if (optionaltag.isPresent()) {

            Tag tag = optionaltag.get();
            model.addAttribute(tag);

            // number or index of the current page to be displayd
            int currentPage = Integer.parseInt(page);
            model.addAttribute("page", currentPage);

            // the page of posts that will be sent to the brownser
            int postPerPage = Integer.parseInt(perPage);            

            // retrive page of post filter by tag
            Pageable paging = PageRequest.of((currentPage-1), postPerPage, Sort.by(sortBy).descending());
            Page<Post> postsBytag = postService.findAllByTagId(tag.getTagId(), paging);
            model.addAttribute("posts", postsBytag);

            // add totalPages to the model
            int totalPages = postsBytag.getTotalPages();
            model.addAttribute("totalPages", totalPages);

            // the last three posts to the model to be shown at the top of the blog
            List<Post> lastThreePosts = postService.findLastThreePublishedPosts();
            model.addAttribute("lastThreePosts", lastThreePosts);

            return "tagView/tag";

        } else {
            return "404";
        }

    }
}
