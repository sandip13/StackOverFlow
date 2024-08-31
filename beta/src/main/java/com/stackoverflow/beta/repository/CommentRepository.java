package com.stackoverflow.beta.repository;

import com.stackoverflow.beta.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM comment where answer_id = :answerId")
    List<Comment> getCommentsByAnswerId(int answerId);
}
