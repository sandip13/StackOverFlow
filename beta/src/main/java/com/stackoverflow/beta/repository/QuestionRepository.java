package com.stackoverflow.beta.repository;

import com.stackoverflow.beta.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @Query(value = "SELECT COUNT(*) FROM question WHERE title = :title AND content = :content AND user_id = :userId", nativeQuery = true)
    int countByTitleContentAndUserId(String title, String content, int userId);


}
