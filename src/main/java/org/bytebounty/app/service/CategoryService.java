package org.bytebounty.app.service;

import java.util.List;
import java.util.Optional;

import org.bytebounty.app.model.Category;
import org.springframework.stereotype.Service;

@Service("categoryService")
public interface CategoryService {

    public Category save(Category category);

    public List<Category> findAll();

    public Optional<Category> findById(Long categoryId);

    public void delete(Category category);

    public Optional<Category> findBySlug(String slug);
    
}
