package org.bytebounty.app.repository;

import java.util.Optional;

import org.bytebounty.app.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository <Category, Long>{

    Optional<Category> findBySlug(String slug);    
}
