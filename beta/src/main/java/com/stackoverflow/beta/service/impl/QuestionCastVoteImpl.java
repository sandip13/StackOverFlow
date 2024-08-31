package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.repository.QuestionRepository;
import com.stackoverflow.beta.service.CastVote;
import com.stackoverflow.beta.utils.CustomPriorityQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class QuestionCastVoteImpl implements CastVote {

    private final QuestionRepository questionRepository;

    private final CustomPriorityQueue customPriorityQueue = CustomPriorityQueue.getCustomPriorityQueue();


    @Autowired
    public QuestionCastVoteImpl(QuestionRepository questionRepository){
        this.questionRepository = questionRepository;
    }

    @Override
    public int upVote(int id) {
        log.info("Attempting to upvote question with ID: {}", id);
        Question question = getQuestion(id);
        question.upvote();
        question = questionRepository.save(question);
        //updating cache
        if(customPriorityQueue.containsKey(question)){
            customPriorityQueue.updateKey(question);
            log.info("Successfully upvoted question with ID: {}. New vote count: {}", id, question.getVotes());
            return question.getVotes();
        }
        customPriorityQueue.add(question);
        log.info("Successfully upvoted question with ID: {}. New vote count: {}", id, question.getVotes());
        return question.getVotes();

    }



    @Override
    public int downVote(int id) {
        log.info("Attempting to downvote question with ID: {}", id);
        Question question = getQuestion(id);
        question.downVote();
        question = questionRepository.save(question);
        //updating cache
        if(customPriorityQueue.containsKey(question)){
            customPriorityQueue.updateKey(question);
            log.info("Successfully downvote question with ID: {}. New vote count: {}", id, question.getVotes());
            return question.getVotes();
        }
        customPriorityQueue.add(question);
        log.info("Successfully downvote question with ID: {}. New vote count: {}", id, question.getVotes());
        return question.getVotes();
    }

    /**
     * Retrieves a question by its ID.
     */
    private Question getQuestion(int id) {
        log.debug("Fetching question with ID: {}", id);
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isPresent()) {
            return optionalQuestion.get();
        }
        log.error("Question with ID: {} not found", id);
        throw new ValidationException("Question not posted so far!!", HttpStatus.BAD_REQUEST);
    }

}