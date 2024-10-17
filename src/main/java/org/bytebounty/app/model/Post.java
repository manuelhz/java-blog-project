package org.bytebounty.app.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "accountId", nullable = false)
    private Account account;

    @NotEmpty(message = "Missing categories")
    @ManyToMany
    @JoinTable(name = "post_category", joinColumns = {
            @JoinColumn(name = "post_id", referencedColumnName = "postId") }, inverseJoinColumns = {
                    @JoinColumn(name = "category_id", referencedColumnName = "categoryId") })
    private Set<Category> categories = new HashSet<>();

    @NotEmpty(message = "Missing tags")
    @ManyToMany
    @JoinTable(name = "post_tag", joinColumns = {
            @JoinColumn(name = "post_id", referencedColumnName = "postId") }, inverseJoinColumns = {
                    @JoinColumn(name = "tag_id", referencedColumnName = "tagId") })
    private Set<Tag> tags = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "parent_post_id", referencedColumnName = "postId", nullable = true)
    private Post parentPost;

    @NotBlank(message = "Missing post title")
    private String title;

    @NotBlank(message = "Missing slug")
    private String slug;

    @NotBlank(message = "Missing summary")
    @Column(length = 5000)
    private String metaSummary;

    // we can create a post as a draft and then when ready we publish it
    private boolean published;

    // if the particular post will show comments at all
    private boolean enableComments;

    // if the particular post will alow to post a new comment
    private boolean enableNewComments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime publishedAt;

    @NotBlank(message = "Missing thumbnail")
    private String featuredImage; // thumbnail

    @NotBlank(message = "Missing image credit")
    @Column(length = 500)
    private String imageCredit;

    @NotBlank(message = "Missing post body")
    @Column(length = 100000)    
    private String content;

    @OneToMany(mappedBy = "parentPost", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<Comment> children;

    public String getCategoriesText() {
        String categories = "";
        int size = this.categories.size();
        Iterator<Category> value = this.categories.iterator();
        for (int i = 0; i < size - 1; i++) {

            categories = categories + value.next().getCategoryName() + ", ";

        }
        if (value.hasNext()) {
            categories = categories + value.next().getCategoryName();
        }

        return categories;
    }

    public int numberOfComments() {

        int comments = 0;

        for (Comment child : children) {

            comments += child.numberOfComments();   

        }     
    
        return comments + this.children.size();
    }

    @Override
    public String toString() {
        return "Post [postId=" + postId + ", title=" + title + ", slug=" + slug + "]";
    }

}