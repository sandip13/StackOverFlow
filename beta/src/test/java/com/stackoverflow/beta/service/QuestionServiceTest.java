package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Question;
import com.stackoverflow.beta.model.QuestionTags;
import com.stackoverflow.beta.model.Tags;
import com.stackoverflow.beta.model.request.PostQuestionInput;
import com.stackoverflow.beta.repository.QuestionRepository;
import com.stackoverflow.beta.repository.QuestionTagRepository;

import com.stackoverflow.beta.service.impl.QuestionServiceImpl;
import com.stackoverflow.beta.utils.CustomPriorityQueue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class QuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private QuestionTagRepository questionTagRepository;
    @Mock
    private UserService userService;
    @Mock
    private TagsService tagsService;

    @InjectMocks
    private QuestionServiceImpl questionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }
    @Test
    void testSaveQuestion_Success() {
        PostQuestionInput input = getPostQuestionInput();
        Question question = savedQuestion();

        when(userService.isUserExist(input.getUserId())).thenReturn(true);
        when(questionRepository.countByTitleContentAndUserId(input.getTitle(), input.getContent(), input.getUserId())).thenReturn(0);
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        Tags tag1 = new Tags(1, "Tag1");
        when(tagsService.getTagByName("Tag1")).thenReturn(tag1);
        when(tagsService.createTag(any(Tags.class))).thenReturn(tag1);

        QuestionTags questionTags = new QuestionTags();
        questionTags.setQuestionId(1);
        questionTags.setTagId(1);
        questionTags.setId(1);

        when(questionTagRepository.save(any(QuestionTags.class))).thenReturn(questionTags);

        Question result = questionService.save(input);
        assertEquals("Title1",result.getTitle());
        assertEquals("Content1",result.getContent());
    }

    @Test
    void testGetQuestionsByTag_Success() {
        String tagInput = "Tag1";
        Tags tag1 = new Tags(1, tagInput);
        Question question = new Question();
        question.setId(1);
        question.setTitle("Test Question");

        when(tagsService.getTagByName(tagInput)).thenReturn(tag1);
        when(questionTagRepository.findByTagId(tag1.getId())).thenReturn(List.of(new QuestionTags(1,1, 1)));
        when(questionRepository.findById(1)).thenReturn(Optional.of(question));

        List<Question> result = questionService.getQuestionsByTag(tagInput);

        assertEquals(1, result.size());
        assertEquals(question.getTitle(), result.get(0).getTitle());
    }

    @Test
    void testGetQuestionById_Success() {
        int questionId = 1;
        Question question = new Question();
        question.setId(questionId);
        question.setTitle("Test Question");

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        Question result = questionService.getQuestionById(questionId);

        assertNotNull(result);
        assertEquals("Test Question", result.getTitle());
    }
    private Question savedQuestion() {
        Question question= new Question();
        question.setId(1);
        question.setContent("Content1");
        question.setTitle("Title1");
        question.setVotes(0);
        return question;
    }

    private PostQuestionInput getPostQuestionInput() {
        PostQuestionInput input = new PostQuestionInput(1, "Title1", "Content1", Arrays.asList("tag1","tag2"));
        return input;
    }
}
