package org.bytebounty.app.controller;


import java.util.List;
import java.util.Optional;

import org.bytebounty.app.model.Category;
import org.bytebounty.app.model.Post;
import org.bytebounty.app.service.CategoryService;
import org.bytebounty.app.service.PostService;
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
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    PostService postService;

    // show categories:
    @GetMapping("/blog/categories")
    public String categories(Model model) {

        List<Category> categories = categoryService.findAll();

        model.addAttribute("categories", categories);

        return "categoryView/categories";
    }

    // edit category
    @GetMapping("/blog/categories/{categoryId}/edit")
    @PreAuthorize("@securityService.hasPrivilege('MANAGE_CATEGORIES')")
    public String editCategory(@PathVariable Long categoryId, Model model) {
        Optional<Category> optionalCategory = categoryService.findById(categoryId);

        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();

            model.addAttribute("category", category);
            return "categoryView/categoryEdit";
        } else {
            return "404";
        }

    }
    @PostMapping("/blog/categories/edit")
    @PreAuthorize("@securityService.hasPrivilege('MANAGE_CATEGORIES')")
    public String editCategoryPost(
        @RequestParam("file") MultipartFile file,
        @Valid @ModelAttribute Category categoryEdited,
        BindingResult result
    ) {
        if (result.hasErrors()) {
            return "categoryView/categoryEdit";
        }
        Optional<Category> optionalCategory = categoryService.findById(categoryEdited.getCategoryId());
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setCategoryDescription(categoryEdited.getCategoryDescription());
            category.setCategoryName(categoryEdited.getCategoryName());
            category.setImageCredit(categoryEdited.getImageCredit());
            category.setParenCategory(categoryEdited.getParenCategory());
            category.setSlug(categoryEdited.getSlug());
            if (!file.isEmpty()) {
                FileStorage.delete(category.getThumbnail());
                category.setThumbnail(FileStorage.save(file, "categories"));
                // wait for the storage to update
                FileStorage.waitt();
            }
            categoryService.save(category);
            
            return "redirect:/blog/categories";
        } else {
            return "404";
        }
    }

    // create category
    @GetMapping("/blog/categories/create")
    @PreAuthorize("@securityService.hasPrivilege('MANAGE_CATEGORIES')")
    public String createCategory(Model model) {
        Category category = new Category();
        model.addAttribute("category", category);
        return "categoryView/categoryCreate";
    }
    @PostMapping("/blog/categories/create")
    @PreAuthorize("@securityService.hasPrivilege('MANAGE_CATEGORIES')")
    public String createCategoryPost(
        @RequestParam("file") MultipartFile file,
        @Valid @ModelAttribute Category newCategory,
        BindingResult result
    ) {
        if (result.hasErrors()) {
            return "categoryView/categoryCreate";
        }

        if (!file.isEmpty()) {
            newCategory.setThumbnail(FileStorage.save(file, "categories"));
            FileStorage.waitt();
        }
        categoryService.save(newCategory);
        
        return "redirect:/blog/categories";        

    }

    // delete Category
    @GetMapping("/blog/categories/{categoryId}/delete")
    @PreAuthorize("@securityService.hasPrivilege('MANAGE_CATEGORIES')")
    public String deleteCategory(@PathVariable("categoryId") Long categoryId, Model model) {
        
        Optional<Category> optionalCategory = categoryService.findById(categoryId);
        if (optionalCategory.isPresent()){
            Category category = optionalCategory.get();
            categoryService.delete(category);
            return "redirect:/blog/categories";
        } else {
            return "404";
        }
    }

    // show category
    @GetMapping("blog/categories/{slug}")
    public String category(
        @PathVariable("slug") String slug, 
        Model model,
        @RequestParam(name = "page", defaultValue = "1", required = false) String page,
        @RequestParam(name = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
        @RequestParam(name = "perPage", defaultValue = "1", required = false) String perPage        
        ) {

        Optional<Category> optionalCategory = categoryService.findBySlug(slug);

        if (optionalCategory.isPresent()) {

            Category category = optionalCategory.get();
            model.addAttribute(category);

            // number or index of the current page to be displayd
            int currentPage = Integer.parseInt(page);
            model.addAttribute("page", currentPage);

            // the page of posts that will be sent to the brownser
            int postPerPage = Integer.parseInt(perPage);            

            // retrive page of post filter by category
            Pageable paging = PageRequest.of((currentPage-1), postPerPage, Sort.by(sortBy).descending());
            Page<Post> postsByCategory = postService.findAllByCategoryId(category.getCategoryId(), paging);
            model.addAttribute("posts", postsByCategory);

            // add totalPages to the model
            int totalPages = postsByCategory.getTotalPages();
            model.addAttribute("totalPages", totalPages);

            // the last three posts to the model to be shown at the top of the blog
            List<Post> lastThreePosts = postService.findLastThreePublishedPosts();
            model.addAttribute("lastThreePosts", lastThreePosts);

            return "categoryView/category";

        } else {
            return "404";
        }

    }

}
