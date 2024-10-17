package org.bytebounty.app.repository;

import java.util.List;

import org.bytebounty.app.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{

    List<Comment> findAllByPostAccountAccountId(Long accountId);
    
}
