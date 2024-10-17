package org.bytebounty.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)    
    Long categoryId;

    // parent category
    @ManyToOne
    @JoinColumn(name = "parent_category_id",
    referencedColumnName = "categoryId",
    nullable = true)
    private Category parenCategory;

    @NotBlank(message = "Missing Category Name")
    String categoryName; // category name

    @Column(length = 5000)
    String categoryDescription; // optional

    @NotBlank(message = "Missing Slug")
    String slug;

    @NotBlank(message = "Missing Thumbnail")
    String thumbnail;

    @NotBlank(message = "Missing image credit")
    @Column(length = 500)
    String imageCredit;
}
