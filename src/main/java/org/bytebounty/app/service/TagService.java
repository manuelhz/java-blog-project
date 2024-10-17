package org.bytebounty.app.service;

import java.util.List;
import java.util.Optional;

import org.bytebounty.app.model.Tag;
import org.springframework.stereotype.Service;

@Service("tagService")
public interface TagService {

    public Tag save(Tag tag);

    public List<Tag> findAll();

    public Optional<Tag> findById(Long tagId);

    public void delete(Tag tag);

    public Optional<Tag> findBySlug(String slug);
    
}