package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.dto.TopQuestionResponse;
import com.stackoverflow.beta.model.request.PostQuestionInput;
import com.stackoverflow.beta.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing questions.
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    private QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService=questionService;
    }

    /**
     * Endpoint for posting a new question.
     *
     * @param postQuestionInput the question input to post
     * @return the saved Question entity
     */
    @PostMapping("/post")
    public ResponseEntity<?> postQuestion(@RequestBody PostQuestionInput postQuestionInput){
        try{
            return new ResponseEntity<>(questionService.save(postQuestionInput), HttpStatus.CREATED);
        } catch (ValidationException e){
            return ResponseEntity.status(e.getStatus())
                    .body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /**
     * Endpoint for finding a question by its ID.
     *
     * @param id the ID of the question to find
     * @return the QuestionResponse containing the question details
     */
    @GetMapping("/findById/{id}")
    ResponseEntity<?> findQuestionById(@PathVariable int id){
        try{
            return new ResponseEntity<>(questionService.getQuestionById(id), HttpStatus.CREATED);
        } catch (ValidationException e){
            return ResponseEntity.status(e.getStatus())
                    .body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /**
     * Endpoint to get the top questions based on the given criteria.
     *
     * @param criteria the criteria for selecting top questions (e.g., "vote" or "timestamp")
     * @param count the criteria for selecting number of top questions (e.g., 10 or 20)
     * @return a ResponseEntity containing the list of top questions
     */
    @GetMapping("/top")
    public ResponseEntity<?> getTopQuestions(@RequestParam String criteria, @RequestParam int count) {
        try{
            List<TopQuestionResponse> topQuestions = questionService.getTopQuestions(criteria, count);
            return new ResponseEntity<>(topQuestions, HttpStatus.OK);
        } catch (ValidationException e){
            return ResponseEntity.status(e.getStatus())
                    .body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /**
     * Endpoint for retrieving questions by tag.
     *
     * @param tag the tag to filter questions by
     * @return a ResponseEntity containing a list of questions associated with the specified tag
     */
    @GetMapping("/tag/{tag}")
    public ResponseEntity<?> getQuestionsByTag(@PathVariable String tag) {
        try{
            return ResponseEntity.ok(questionService.getQuestionsByTag(tag));
        } catch (ValidationException e){
            return ResponseEntity.status(e.getStatus())
                    .body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
