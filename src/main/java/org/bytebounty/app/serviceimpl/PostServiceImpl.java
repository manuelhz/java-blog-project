package org.bytebounty.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bytebounty.app.model.Post;
import org.bytebounty.app.repository.PostRepository;
import org.bytebounty.app.service.PostService;
import org.bytebounty.app.util.FileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service("postService")
public class PostServiceImpl implements PostService {

    @Autowired
    PostRepository postRepository;

    @Override
    public Post save(Post post) {

        // if it is a new post
        if (post.getPostId() == null) {
            // Set createdAt
            post.setCreatedAt(LocalDateTime.now());
        }

        // Set updatedAt
        post.setUpdatedAt(LocalDateTime.now());
        
        // Set publishedAt
        if (post.getPublishedAt() == null && post.isPublished() == true) {
            post.setPublishedAt(LocalDateTime.now());
        }
        String slug = post
        .getSlug().toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "") // remove non-alphanumeric characters except spaces
        .replaceAll("\\s", "-"); // replace spaces with hyphens
        post.setSlug(slug);
        


        return postRepository.save(post);        
    }

    @Override
    public void delete(Post post) {

        // find parent or children posts and make the relation null
        Optional<Post> optionalSibling = this.findSibling(post);
            if (optionalSibling.isPresent()){
                
                Post relative = optionalSibling.get();
                relative.setParentPost(null);
                this.save(relative);

            }

        // delete post images from storage
        FileStorage.delete(post.getFeaturedImage());
        for (String img : FileStorage.imageTagExtractor(post.getContent())) {
            FileStorage.delete(img);
        }
        // delete post from database
        postRepository.delete(post);
    }

    @Override
    public List<Post> findAll() {

        return postRepository.findAll();

    }

    @Override
    public Page<Post> findAll(int offset, int pageSize, String field) {
        return postRepository.findAll(PageRequest.of(offset, pageSize).withSort(Direction.DESC, field));
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return postRepository.findById(postId);
    }

    @Override
    public List<Post> findLastThreePublishedPosts() {
        return postRepository.findLastThreePublishedPosts();
    }

    @Override
    public Boolean isLastPost(Long postId) {
        return postId == postRepository.findLastPostId();
    }

    @Override
    public Boolean isFirstPost(Long postId) {
        return postId == postRepository.findFirstPostId();
    }

    @Override
    public Optional<Post> findSibling(Post post) {
        return postRepository.findSibling(post);
    }

    @Override
    public Optional<Post> findNextById(Long postId) {
        return postRepository.findNextById(postId);
    }

    @Override
    public Optional<Post> findPreviousById(Long postId) {
        return postRepository.findPreviousById(postId);
    }

    @Override
    public List<Post> findAllByTagId(Long tagId) {
        return postRepository.findAllByTagsTagId(tagId);
    }

    @Override
    public List<Post> findAllByCategoryId(Long categoryId) {
        return postRepository.findAllByCategoriesCategoryId(categoryId);
    }

    @Override
    public List<Post> findAllByAccountId(Long accountId) {
        return  postRepository.findAllByAccountAccountId(accountId);
    }


    @Override
    public Page<Post> findAllByCategoryId(Long categoryId, Pageable pageable) {
        return postRepository.findAllByCategoriesCategoryId(categoryId, pageable);
    }

    @Override
    public Page<Post> findAllByTagId(Long tagId, Pageable paging) {
        return postRepository.findAllByTagsTagId(tagId, paging);
    }

    @Override
    public Optional<Post> findBySlug(String slug) {
        return postRepository.findBySlug(slug);
    }

    @Override
    public Page<Post> findByPublishedTrue(Pageable paging) {
        return postRepository.findByPublishedTrue(paging);
    }
}
