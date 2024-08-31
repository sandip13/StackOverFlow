package com.stackoverflow.beta.service;

import com.stackoverflow.beta.PostType;
import com.stackoverflow.beta.service.impl.VoteFactory;
import com.stackoverflow.beta.service.impl.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

public class VoteServiceTest {
    @Mock
    private VoteFactory voteFactory;

    @Mock
    private CastVote castVoteService;

    @InjectMocks
    private VoteService voteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpvote_Success() {

        int postId = 123;
        PostType postType = PostType.QUESTION;
        when(voteFactory.getVoteService(any(PostType.class))).thenReturn(castVoteService);
        when(castVoteService.upVote(anyInt())).thenReturn(1);

        int result = voteService.upvote(postType, postId);

        assertEquals(1, result);
    }

    @Test
    void testDownvote_Success() {
        int postId = 456;
        PostType postType = PostType.ANSWER;
        when(voteFactory.getVoteService(any(PostType.class))).thenReturn(castVoteService);
        when(castVoteService.downVote(postId)).thenReturn(-1);

        int result = voteService.downvote(postType, postId);

        assertEquals(-1, result, "Downvote should return the correct vote count");
    }

}
