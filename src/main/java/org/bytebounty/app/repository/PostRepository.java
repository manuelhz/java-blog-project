package org.bytebounty.app.repository;

import java.util.List;
import java.util.Optional;

import org.bytebounty.app.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository <Post, Long>{    

    @Query("select p from Post p where p.published = ?#{true} order by p.publishedAt desc limit 3")
    List<Post> findLastThreePublishedPosts();

    @Query("select postId from Post p order by p.postId desc limit 1")
    Long findLastPostId();

    @Query("select postId from Post p order by p.postId asc limit 1")
    Long findFirstPostId();

    @Query("select p from Post p where p.parentPost.postId = :#{#post.postId}")
    Optional<Post> findSibling(@Param("post") Post post);

    @Query("select p from Post p where p.postId > ?#{[0]} order by p.postId asc limit 1")
    Optional<Post> findPreviousById(Long postId);

    @Query("select p from Post p where p.postId < ?#{[0]} order by p.postId desc limit 1")
    Optional<Post> findNextById(Long postId);

    List<Post> findAllByTagsTagId(Long tagId);

    List<Post> findAllByCategoriesCategoryId(Long categoryId);

    List<Post> findAllByAccountAccountId(Long accountId);

    Page<Post> findAllByCategoriesCategoryId(Long categoryId, Pageable pageable);

    Page<Post> findAllByTagsTagId(Long tagId, Pageable paging);

    Optional<Post> findBySlug(String slug);

    Page<Post> findByPublishedTrue(Pageable paging);

}