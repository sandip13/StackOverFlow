package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.PostType;
import com.stackoverflow.beta.service.CastVote;
import org.springframework.stereotype.Component;

@Component
public class VoteFactory {

    private final QuestionCastVoteImpl questionVoteService;
    private final AnswerCastVoteImpl answerVoteService;

    public VoteFactory(QuestionCastVoteImpl questionVoteService, AnswerCastVoteImpl answerVoteService) {
        this.questionVoteService = questionVoteService;
        this.answerVoteService = answerVoteService;
    }

    public CastVote getVoteService(PostType postType) {
        switch (postType) {
            case QUESTION:
                return questionVoteService;
            case ANSWER:
                return answerVoteService;
            default:
                throw new IllegalArgumentException("Invalid PostType: " + postType);
        }
    }
}
