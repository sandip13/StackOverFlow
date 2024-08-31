package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.Comment;
import com.stackoverflow.beta.model.User;
import com.stackoverflow.beta.model.request.PostCommentInput;
import com.stackoverflow.beta.repository.AnswerRepository;
import com.stackoverflow.beta.repository.CommentRepository;
import com.stackoverflow.beta.repository.UserRepository;
import com.stackoverflow.beta.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveComment_Success() {
        PostCommentInput input = new PostCommentInput(1, "first comment", 1);
        User user = new User();
        user.setId(input.getUserId());
        Answer answer = new Answer();
        answer.setId(input.getAnswerId());

        Comment commentResponse = new Comment();
        commentResponse.setAnswer(answer);
        commentResponse.setId(1);
        commentResponse.setText("first comment");
        commentResponse.setUserId(input.getUserId());

        when(userRepository.findById(input.getUserId())).thenReturn(Optional.of(user));
        when(answerRepository.findById(input.getAnswerId())).thenReturn(Optional.of(answer));
        when(commentRepository.save(any())).thenReturn(commentResponse);

        Comment result = commentService.save(input);
        assertEquals(answer, result.getAnswer());
        assertEquals("first comment",result.getText());

    }
}
