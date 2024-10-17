package org.bytebounty.app.model;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long commentId;

    private boolean published;

    // relation to the parent post
    @ManyToOne
    @JoinColumn(name="parent_post_id",
    referencedColumnName = "postId",
    nullable = true)
    private Post parentPost;

    // relation to the children comment
    @OneToMany(mappedBy = "parentComment",cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private Set<Comment> children;

    // relation to the parent coment
    @ManyToOne
    @JoinColumn(name="parent_comment_id",
    referencedColumnName = "commentId",
    nullable = true)
    private Comment parentComment;
    
    

    // User account
    @ManyToOne    
    private Account postAccount;

    String title;

    String content;

    LocalDateTime publishedAt;

    LocalDateTime editedAt;

    public boolean hasChildren() {
        if (this.children.size() > 0) {
            return true;
        } else return false;

    }

    public int numberOfComments() {
        return children.size();
    }
}