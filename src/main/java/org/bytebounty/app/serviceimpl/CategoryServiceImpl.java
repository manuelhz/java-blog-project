package org.bytebounty.app.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bytebounty.app.model.Category;
import org.bytebounty.app.model.Post;
import org.bytebounty.app.repository.CategoryRepository;
import org.bytebounty.app.service.CategoryService;
import org.bytebounty.app.service.PostService;
import org.bytebounty.app.util.FileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    PostService postService;

    @Override
    public Category save(Category category) {

        return categoryRepository.save(category);
        
    }

    @Override
    public List<Category> findAll() {

        return categoryRepository.findAll();
        
    }

    @Override
    public Optional<Category> findById(Long categoryId) {
        
        return categoryRepository.findById(categoryId);
    }

    @Override
    public void delete(Category category) {

        // before deletting a category I nee to delet them from post
        // find all post with this category and remove category from them
        List<Post> relatedPosts = postService.findAllByCategoryId(category.getCategoryId());
        if(relatedPosts.size() > 0) {
            for (Post post: relatedPosts) {
                Set<Category> categories = post.getCategories();
                categories.remove(category);
                post.setCategories(categories);
                postService.save(post);
            }
        }
        FileStorage.delete(category.getThumbnail());

        categoryRepository.delete(category);
        
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }
    
}