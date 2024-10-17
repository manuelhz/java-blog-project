package org.bytebounty.app.serviceimpl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bytebounty.app.model.Post;
import org.bytebounty.app.model.Tag;
import org.bytebounty.app.repository.TagRepository;
import org.bytebounty.app.service.PostService;
import org.bytebounty.app.service.TagService;
import org.bytebounty.app.util.FileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("tagService")
public class TagServiceImpl implements TagService {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostService postService;

    @Override
    public Tag save(Tag tag) {

        return tagRepository.save(tag);
        
    }

    @Override
    public List<Tag> findAll() {
        return tagRepository.findAll();
    }

    @Override
    public Optional<Tag> findById(Long tagId) {
        return tagRepository.findById(tagId);
    }

    @Override
    public void delete(Tag tag) {
        // before delete a tag I need to unrelate them form post
        // find all post that have this tag and delete them from the list
        List<Post> relatedPosts = postService.findAllByTagId(tag.getTagId());
        if(relatedPosts.size()>0) {
            for (Post post: relatedPosts) {
                Set<Tag> tags = post.getTags();
                tags.remove(tag);
                post.setTags(tags);
                postService.save(post);
            }
        }
        FileStorage.delete(tag.getThumbnail());
        
        tagRepository.delete(tag);
    }

    @Override
    public Optional<Tag> findBySlug(String slug) {
        return tagRepository.findBySlug(slug);
    }    
}
