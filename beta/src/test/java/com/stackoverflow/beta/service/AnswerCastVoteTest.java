package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.repository.AnswerRepository;
import com.stackoverflow.beta.service.impl.AnswerCastVoteImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class AnswerCastVoteTest {
    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerCastVoteImpl castVoteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpVote_Success() {
        int answerId = 1;
        Answer answer = new Answer();
        answer.setVotes(5);
        Answer savedAnswer = new Answer();
        savedAnswer.setVotes(6);

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));
        when(answerRepository.save(answer)).thenReturn(savedAnswer);

        int newVoteCount = castVoteService.upVote(answerId);
        assertEquals(6, newVoteCount);
    }

    @Test
    void testDownVote_Success() {
        int answerId = 1;
        Answer answer = new Answer();
        answer.setVotes(5);

        Answer savedAnswer = new Answer();
        savedAnswer.setVotes(4);

        when(answerRepository.findById(answerId)).thenReturn(Optional.of(answer));
        when(answerRepository.save(answer)).thenReturn(savedAnswer);

        int newVoteCount = castVoteService.downVote(answerId);

        assertEquals(4, newVoteCount);

    }
}
