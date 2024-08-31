package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Comment;
import com.stackoverflow.beta.model.request.PostCommentInput;
import com.stackoverflow.beta.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing comment.
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Endpoint to post a comment.
     *
     * @param postCommentInput - the input object containing comment details
     * @return ResponseEntity containing the created Comment object or error message
     */
    @PostMapping(path = "/post")
    public ResponseEntity<?> postComment(@RequestBody PostCommentInput postCommentInput){
        try{
            Comment comment = commentService.save(postCommentInput);
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        }catch (ValidationException e){
            return ResponseEntity.status(e.getStatus())
                    .body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

}
