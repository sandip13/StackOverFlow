package com.stackoverflow.beta.controller;


import com.stackoverflow.beta.PostType;
import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.service.impl.VoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/vote")
public class VoteController {

    private final VoteService votingService;

    @Autowired
    public VoteController (VoteService votingService){
        this.votingService = votingService;
    }

    /**
     * Endpoint to upvote a post (either a question or an answer).
     *
     * @param postType - the type of post to vote for (question or answer)
     * @param postId - the ID of the post to be upvoted
     * @return the updated vote count after the upvote
     */
    @PostMapping("/upVote")
    public ResponseEntity<?> upVote(@RequestParam PostType postType, @RequestParam int postId){
        try{
            return new ResponseEntity<>(votingService.upvote(postType, postId), HttpStatus.OK);
        } catch (ValidationException e){
            return ResponseEntity.status(e.getStatus())
                    .body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /**
     * Endpoint to downvote a post (either a question or an answer).
     *
     * @param postType - the type of post to vote for (question or answer)
     * @param postId - the ID of the post to be downvoted
     * @return the updated vote count after the downvote
     */
    @PostMapping("/downVote")
    public ResponseEntity<?> downVote(@RequestParam PostType postType, @RequestParam int postId){
        try{
            return new ResponseEntity<>(votingService.downvote(postType,postId),HttpStatus.OK);
        } catch (ValidationException e){
            return ResponseEntity.status(e.getStatus())
                    .body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
