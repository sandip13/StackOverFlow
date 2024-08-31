package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.PostType;
import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.service.impl.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class VoteControllerTest {
    @Mock
    private VoteService votingService;

    @InjectMocks
    private VoteController voteController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpVote_Success() {

        int updatedVoteCount = 10;
        when(votingService.upvote(any(PostType.class), anyInt())).thenReturn(updatedVoteCount);

        ResponseEntity<?> response = voteController.upVote(PostType.QUESTION, 1);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedVoteCount, response.getBody());
    }

    @Test
    void testDownVote_Success() {

        int updatedVoteCount = 10;
        when(votingService.downvote(any(PostType.class), anyInt())).thenReturn(updatedVoteCount);

        ResponseEntity<?> response = voteController.downVote(PostType.QUESTION, 1);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedVoteCount, response.getBody());
    }

    @Test
    void testUpVote_Exception() {

        when(votingService.upvote(any(PostType.class), anyInt())).thenThrow(new ValidationException("Invalid request",  HttpStatus.BAD_REQUEST));

        ResponseEntity<?> response = voteController.upVote(PostType.ANSWER, 1);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request", response.getBody());
    }

    @Test
    void testDownVote_Exception() {

        when(votingService.downvote(any(PostType.class), anyInt())).thenThrow(new ValidationException("Invalid request",  HttpStatus.BAD_REQUEST));

        ResponseEntity<?> response = voteController.downVote(PostType.ANSWER, 1);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid request", response.getBody());
    }
}
