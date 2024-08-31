package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Tags;
import com.stackoverflow.beta.repository.TagRepository;
import com.stackoverflow.beta.service.impl.TagsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class TagServiceTest {
    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagsServiceImpl tagsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTagByName_Success() {
        String tagName = "Java";
        Tags expectedTag = new Tags();
        expectedTag.setId(1);
        expectedTag.setName(tagName);

        when(tagRepository.findByName(tagName)).thenReturn(expectedTag);

        Tags result = tagsService.getTagByName(tagName);

        assertNotNull(result);
        assertEquals(tagName, result.getName());
    }

    @Test
    void testCreateTag_Success() {
        Tags newTag = new Tags();
        newTag.setName("Java");

        Tags savedTag = new Tags();
        savedTag.setId(1);
        savedTag.setName("Java");

        when(tagRepository.save(newTag)).thenReturn(savedTag);

        Tags result = tagsService.createTag(newTag);

        assertNotNull(result);
        assertEquals("Java", result.getName());
    }
}
