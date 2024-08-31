package com.stackoverflow.beta.repository;

import com.stackoverflow.beta.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM answer where question_id = :questionId")
    List<Answer> getAnswerByQuestionId(int questionId);
}
