package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Comment;
import com.stackoverflow.beta.model.request.PostCommentInput;
import com.stackoverflow.beta.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CommentControllerTest {
    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPostComment_Success() {
        PostCommentInput postCommentInput = mockCommentRequest();
        Comment mockCommentResponse = mockCommentResponse();

        when(commentService.save(any(PostCommentInput.class))).thenReturn(mockCommentResponse);

        ResponseEntity<?> response = commentController.postComment(postCommentInput);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockCommentResponse, response.getBody());
    }

    @Test
    void testPostComment_Failure() {
        PostCommentInput postCommentInput = mockCommentRequest();

        when(commentService.save(any(PostCommentInput.class))).thenThrow(new ValidationException("User not registered.", HttpStatus.BAD_REQUEST));

        ResponseEntity<?> response = commentController.postComment(postCommentInput);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not registered.", response.getBody());
    }


    private Comment mockCommentResponse() {
        Comment mockCommentResponse = new Comment();
        mockCommentResponse.setId(1);
        mockCommentResponse.setUserId(1);
        mockCommentResponse.setText("This is a test comment.");
        return mockCommentResponse;
    }

    private PostCommentInput mockCommentRequest() {
        PostCommentInput postCommentInput = new PostCommentInput();
        postCommentInput.setUserId(1);
        postCommentInput.setAnswerId(1);
        postCommentInput.setComment("This is a test comment.");
        return postCommentInput;
    }

}
