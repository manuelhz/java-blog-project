package org.bytebounty.app.service;

import java.util.List;
import java.util.Optional;

import org.bytebounty.app.model.Comment;
import org.springframework.stereotype.Service;

@Service("commentService")
public interface CommentService {

    public Comment save(Comment postComment);

    public Optional<Comment> findById(Long commentId);

    public List<Comment> findByAccountId(Long accountId);

    public void delete(Comment comment);
    
}
