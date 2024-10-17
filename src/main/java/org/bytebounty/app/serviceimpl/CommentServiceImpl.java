package org.bytebounty.app.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bytebounty.app.model.Comment;
import org.bytebounty.app.repository.CommentRepository;
import org.bytebounty.app.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service("commentService")
public class CommentServiceImpl implements CommentService{

    @Autowired
    CommentRepository commentRepository;

    @Override
    public Comment save(Comment postComment) {

        if (postComment.getPublishedAt() == null) {

            postComment.setPublishedAt(LocalDateTime.now());
            
            postComment.setEditedAt(LocalDateTime.now());

        } else {

            postComment.setEditedAt(LocalDateTime.now());
        }

        
        return commentRepository.save(postComment);

    }

    @Override
    public Optional<Comment> findById(Long commentId) {
        
        return commentRepository.findById(commentId);
    }

    @Override
    public List<Comment> findByAccountId(Long accountId) {
        return commentRepository.findAllByPostAccountAccountId(accountId);
    }

    @Override
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }
    
}
