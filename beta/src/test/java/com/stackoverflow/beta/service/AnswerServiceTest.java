package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.User;
import com.stackoverflow.beta.model.request.PostAnswerInput;
import com.stackoverflow.beta.repository.AnswerRepository;
import com.stackoverflow.beta.repository.QuestionRepository;
import com.stackoverflow.beta.service.impl.AnswerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AnswerServiceTest {
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private UserService userService;


    @InjectMocks
    private AnswerServiceImpl answerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave_Success() {
        PostAnswerInput input = new PostAnswerInput(1, 2, "Test Answer");
        Question question = new Question();
        question.setId(1);
        User user = new User();
        user.setId(1);

        Answer answer = new Answer();
        answer.setId(1);
        answer.setContent("Test Answer");
        answer.setVotes(0);
        answer.setUserId(1);
        answer.setQuestion(question);


        when(questionRepository.findById(input.getQuestionId())).thenReturn(Optional.of(question));
        when(userService.getUserById(input.getUserId())).thenReturn(Optional.of(user));
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);
        Answer response = answerService.save(input);
        assertEquals("Test Answer",response.getContent());
        assertEquals(question,response.getQuestion());

    }

    @Test
    void testGetAnswerById_Success() {
        int answerId = 1;
        Answer expectedAnswer = new Answer();
        expectedAnswer.setId(answerId);
        when(answerRepository.findById(answerId)).thenReturn(Optional.of(expectedAnswer));

        Answer result = answerService.getAnswerById(answerId);
        assertEquals(answerId, result.getId());
    }

}
