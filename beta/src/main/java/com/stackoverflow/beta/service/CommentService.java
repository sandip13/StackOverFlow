package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.Comment;
import com.stackoverflow.beta.model.request.PostCommentInput;


public interface CommentService {

    /**
     * Saves a new comment based on the provided input.
     *
     * @param commentInput - the input object containing comment details
     * @return the saved Comment object
     */
     Comment save(PostCommentInput commentInput);

}
