package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.PostType;
import com.stackoverflow.beta.service.CastVote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteService {
    private final VoteFactory voteFactory;

    @Autowired
    public VoteService(VoteFactory voteFactory) {
        this.voteFactory = voteFactory;
    }

    public int upvote(PostType postType, int postId) {
        CastVote castVote = voteFactory.getVoteService(postType);
        return castVote.upVote(postId);
    }

    public int downvote(PostType postType, int postId) {
        CastVote castVote = voteFactory.getVoteService(postType);
        return castVote.downVote(postId);
    }
}
