package com.stackoverflow.beta.service;

public interface CastVote {

    /**
     * Upvotes  and returns the updated vote count.
     *
     * @param postId the ID of the answer/question to be upvoted
     * @return the updated vote count
     */
    public int upVote(int postId);

    /**
     * Downvotes  and returns the updated vote count.
     *
     * @param postId the ID of the answer/question to be downvoted
     * @return the updated vote count
     */
    public int downVote(int postId);
}
