package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.request.PostAnswerInput;
import com.stackoverflow.beta.service.AnswerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class AnswerControllerTest {
    @Mock
    private AnswerService answerService;

    @InjectMocks
    private AnswerController answerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPostAnswer_Success() {
        PostAnswerInput postAnswerInput = getPostAnswerInput();

        Answer mockResponse = getMockAnswerResponse();

        when(answerService.save(any(PostAnswerInput.class))).thenReturn(mockResponse);

        ResponseEntity<?> response = answerController.postAnswer(postAnswerInput);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockResponse, response.getBody());
    }

    @Test
    void testPostAnswer_Failure() {
        PostAnswerInput postAnswerInput = getPostAnswerInput();
        ValidationException validationException = new ValidationException("Question doesn't exist", HttpStatus.BAD_REQUEST);
        when(answerService.save(any(PostAnswerInput.class))).thenThrow(validationException);

        ResponseEntity<?> response = answerController.postAnswer(postAnswerInput);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Question doesn't exist", response.getBody());
    }

    @Test
    void testPostAnswerWithMedia_Success() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());
        String content = "Test Content";

        Answer answer = getMockAnswerResponse();
        when(answerService.saveWithMedia(any(MultipartFile.class), eq(content))).thenReturn(answer);
        ResponseEntity<?> response = answerController.postAnswer(file, content);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(answer, response.getBody());
    }

    @Test
    void testPostAnswerWithMedia_Failure() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());
        String content = "mock answer";

        ValidationException validationException = new ValidationException("Question doesn't exist", HttpStatus.BAD_REQUEST);
        when(answerService.saveWithMedia(any(MultipartFile.class), eq(content))).thenThrow(validationException);

        ResponseEntity<?> response = answerController.postAnswer(file, content);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Question doesn't exist", response.getBody());
    }

    private Answer getMockAnswerResponse() {
        Answer answer = new Answer();
        answer.setId(1);
        answer.setContent("Test Content");
        return answer;
    }

    private PostAnswerInput getPostAnswerInput() {
        PostAnswerInput postAnswerInput = new PostAnswerInput();
        postAnswerInput.setAnswer("Mock Answer");
        return postAnswerInput;
    }
}
