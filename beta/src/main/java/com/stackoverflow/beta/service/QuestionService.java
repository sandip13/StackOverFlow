package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.dto.TopQuestionResponse;
import com.stackoverflow.beta.model.request.PostQuestionInput;

import java.util.List;

public interface QuestionService {

    /**
     * Saves a new question.
     *
     * @param postQuestionInput the input containing question details to be saved
     * @return the saved Question entity
     */
    Question save(PostQuestionInput postQuestionInput);

    /**
     * Retrieves questions associated with a specific tag.
     *
     * @param tag the tag to filter questions by
     * @return a list of Question entities associated with the specified tag
     */
    List<Question> getQuestionsByTag(String tag);

    /**
     * Retrieves a question by its ID.
     *
     * @param id the ID of the question to retrieve
     * @return a QuestionResponse containing the question details, or null if not found
     */
    Question getQuestionById(int id);

    List<TopQuestionResponse> getTopQuestions(String criteria, int count);

}
