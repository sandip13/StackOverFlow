package com.stackoverflow.beta.repository;

import com.stackoverflow.beta.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tags, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM tags where name = :tagName")
    Tags findByName(String tagName);

    @Query(nativeQuery = true, value = "select t.name from tags t,question_tags qt where t.id=qt.tag_id and qt.question_id = :questionId")
    List<String> getTagNamesByQuestionId(int questionId);
}
