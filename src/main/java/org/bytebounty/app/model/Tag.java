package org.bytebounty.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)    
    Long tagId;

    @NotBlank(message = "Missing Tag Name")
    String tagName; // tag name

    @Column(length = 5000)
    String tagDescription; // optional

    @NotBlank(message = "Missing Slug")
    String slug;

    @NotBlank(message = "Missing Thumbnail")
    String thumbnail;

    @NotBlank(message = "Missing image credit")
    @Column(length = 500)
    String imageCredit;
    
}
