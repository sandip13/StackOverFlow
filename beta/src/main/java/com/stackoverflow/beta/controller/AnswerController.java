package com.stackoverflow.beta.controller;
import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Answer;
import com.stackoverflow.beta.model.request.PostAnswerInput;
import com.stackoverflow.beta.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for managing answer.
 */
@RestController
@RequestMapping("/answer")
public class AnswerController {

    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    /**
     * Endpoint to post an answer.
     *
     * @param postAnswerInput - the input object containing answer details
     * @return ResponseEntity containing the created Answer object or error message
     */
    @PostMapping(path = "/post")
    public ResponseEntity<?> postAnswer( @RequestBody PostAnswerInput postAnswerInput){
        try{
            Answer answer = answerService.save(postAnswerInput);
            return new ResponseEntity<>(answer, HttpStatus.CREATED);
        } catch (ValidationException e){
            return ResponseEntity.status(e.getStatus())
                    .body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }

    }

    /**
     * Endpoint for posting an answer with media.
     *
     * @param file - The multipart file representing the media content.
     * @param postAnswerInput - The string representing the answer content.
     * @return ResponseEntity containing the created answer object or error message
     */

    @PostMapping(path = "/postWithMedia")
    public ResponseEntity<?> postAnswer(@RequestParam ("file") MultipartFile file , @RequestParam("data") String postAnswerInput){
        try{
            Answer answer = answerService.saveWithMedia(file, postAnswerInput);
            return new ResponseEntity<>(answer, HttpStatus.CREATED);
        } catch (ValidationException e){
            return ResponseEntity.status(e.getStatus())
                    .body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /**
     * Endpoint for fetching an answer by ida.
     *
     * @param id - The ID of the answer to find
     * @return the Answer details
     */

    @GetMapping("/findById/{id}")
    ResponseEntity<?> findAnswerById(@PathVariable int id){
        try{
            return new ResponseEntity<>(answerService.getAnswerById(id), HttpStatus.CREATED);
        } catch (ValidationException e){
            return ResponseEntity.status(e.getStatus())
                    .body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

}