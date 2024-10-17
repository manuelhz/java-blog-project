package org.bytebounty.app.service;

import java.util.List;

import java.util.Optional;
import org.bytebounty.app.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service("postService")
public interface PostService {
    
    public Post save(Post post);

    public void delete(Post post);

    public List<Post> findAll();

    public Page<Post> findAll(int offset, int pageSize, String field);

    public Optional<Post> findById(Long postId);

    public List<Post> findLastThreePublishedPosts();

    public Boolean isLastPost(Long postId);

    public Boolean isFirstPost(Long postId);

    public Optional<Post> findSibling(Post post);

    public Optional<Post> findNextById(Long postId);

    public Optional<Post> findPreviousById(Long postId);

    public List<Post> findAllByTagId(Long tagId);

    public List<Post> findAllByCategoryId(Long categoryId);

    public List<Post> findAllByAccountId(Long accountId);

    public Page<Post> findAllByCategoryId(Long categoryId, Pageable paging);

    public Page<Post> findAllByTagId(Long tagId, Pageable paging);

    public Optional<Post> findBySlug(String slug);

    public Page<Post> findByPublishedTrue(Pageable paging);

    
}