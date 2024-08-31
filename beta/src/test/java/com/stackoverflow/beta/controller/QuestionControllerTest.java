package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.dto.TopQuestionResponse;
import com.stackoverflow.beta.model.request.PostQuestionInput;
import com.stackoverflow.beta.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class QuestionControllerTest {
    @Mock
    private QuestionService questionService;

    @InjectMocks
    private  QuestionController questionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPostQuestion_Success() {
        PostQuestionInput postQuestionInput = getPostQuestionInput();

        Question mockQuestionResponse = getQuestionResponse();

        when(questionService.save(any(PostQuestionInput.class))).thenReturn(mockQuestionResponse);

        ResponseEntity<?> response = questionController.postQuestion(postQuestionInput);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockQuestionResponse, response.getBody());
    }

    @Test
    void testPostQuestion_Failure() {
        PostQuestionInput postQuestionInput = getPostQuestionInput();

        ValidationException validationException = new ValidationException("User not registered!!", HttpStatus.BAD_REQUEST);
        when(questionService.save(any(PostQuestionInput.class))).thenThrow(validationException);

        ResponseEntity<?> response = questionController.postQuestion(postQuestionInput);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not registered!!", response.getBody());
    }

    @Test
    void testFindQuestionById_Success() {
        Question questionResponse = getQuestionResponse();
        when(questionService.getQuestionById(anyInt())).thenReturn(questionResponse);

        ResponseEntity<?> response = questionController.findQuestionById(1);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(questionResponse, response.getBody());
    }

    @Test
    void testFindQuestionById_Failure() {
        ValidationException validationException = new ValidationException("Invalid question ID", HttpStatus.BAD_REQUEST);
        when(questionService.getQuestionById(anyInt())).thenThrow(validationException);

        ResponseEntity<?> response = questionController.findQuestionById(1);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid question ID", response.getBody());
    }

    @Test
    void testGetTopQuestions_Success() {
        // Mock top questions
        List<TopQuestionResponse> topQuestions = new ArrayList<>();
        mockTopListQuestionsResponse(topQuestions);

        when(questionService.getTopQuestions(anyString(),anyInt())).thenReturn(topQuestions);

        ResponseEntity<?> response = questionController.getTopQuestions("vote", 5);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(topQuestions, response.getBody());
    }

    @Test
    void testGetTopQuestions_Failure() {
        when(questionService.getTopQuestions(anyString(), anyInt())).thenThrow(new RuntimeException("Internal server error"));

        ResponseEntity<?> response = questionController.getTopQuestions("vote", 5);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody());
    }

    @Test
    void testGetQuestionsByTag_Success() {
        List<Question> questionsByTag = new ArrayList<>();
        mockListQuestionsResponse(questionsByTag);

        when(questionService.getQuestionsByTag(anyString())).thenReturn(questionsByTag);

        ResponseEntity<?> response = questionController.getQuestionsByTag("java");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(questionsByTag, response.getBody());
    }

    @Test
    void testGetQuestionsByTag_Failure() {
        when(questionService.getQuestionsByTag(anyString())).thenThrow(new ValidationException("Invalid Tag", HttpStatus.BAD_REQUEST));

        ResponseEntity<?> response = questionController.getQuestionsByTag("java");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Tag", response.getBody());
    }


    private void mockTopListQuestionsResponse(List<TopQuestionResponse> topQuestions) {
        TopQuestionResponse question1 = new TopQuestionResponse();
        question1.setTitle("Title 1");
        question1.setUser(1);
        question1.setVotes(1);
        TopQuestionResponse question2 = new TopQuestionResponse();
        question2.setTitle("Title 2");
        question1.setUser(1);
        question1.setVotes(0);
        topQuestions.add(question1);
        topQuestions.add(question2);
    }

    private void mockListQuestionsResponse(List<Question> topQuestions) {
        Question question1 = new Question();
        question1.setTitle("Title 1");
        question1.setContent("Content 1");
        question1.setId(1);
        Question question2 = new Question();
        question2.setTitle("Title 2");
        question1.setContent("Content 2");
        question1.setId(2);
        topQuestions.add(question1);
        topQuestions.add(question2);
    }

    private Question getQuestionResponse() {
        Question mockQuestionResponse = new Question();
        mockQuestionResponse.setId(1);
        mockQuestionResponse.setTitle("Mock Title");
        mockQuestionResponse.setContent("Mock Content");
        return mockQuestionResponse;
    }

    private PostQuestionInput getPostQuestionInput() {
        PostQuestionInput postQuestionInput = new PostQuestionInput();
        postQuestionInput.setTitle("Mock Title");
        postQuestionInput.setContent("Mock Content");
        return postQuestionInput;
    }
}
