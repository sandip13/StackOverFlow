package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.repository.AnswerRepository;
import com.stackoverflow.beta.service.CastVote;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AnswerCastVoteImpl implements CastVote {

    private final AnswerRepository answerRepository;

    public AnswerCastVoteImpl(AnswerRepository repository){
        this.answerRepository = repository;
    }
    @Override
    public int upVote(int id) {
        log.info("Attempting to upvote answer with ID: {}", id);
        Answer answer = getAnswer(id);
        answer.upvote();
        answerRepository.save(answer);
        log.info("Successfully upvoted answer with ID: {}. New vote count: {}", id, answer.getVotes());
        return answer.getVotes();
    }
    @Override
    public int downVote(int id) {
        log.info("Attempting to downvote answer with ID: {}", id);
        Answer answer = getAnswer(id);
        answer.downVote();
        answerRepository.save(answer);
        log.info("Successfully downvoted answer with ID: {}. New vote count: {}", id, answer.getVotes());
        return answer.getVotes();
    }

    /**
     * Retrieves an answer by its ID.
     */
    private Answer getAnswer(int id) {
        log.debug("Fetching answer with ID: {}", id);
        Optional<Answer> optionalAnswer = answerRepository.findById(id);
        if(optionalAnswer.isEmpty())
        {
            log.error("Answer with ID: {} not found", id);
            throw new ValidationException("Answer not posted so far!!", HttpStatus.BAD_REQUEST);
        }
        return optionalAnswer.get();
    }


}