package com.stackoverflow.beta.controller;
import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.request.UserDetailsInput;
import com.stackoverflow.beta.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing user.
 */

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController (UserService userService){
        this.userService=userService;
    }

    /**
     * Registers a new user.
     *
     * @param userDetailsInput the user information to register
     * @return a ResponseEntity with the result of the saved user
     * @throws Exception if an error occurs during registration
     */
    @PostMapping(path="/register")
    public ResponseEntity<?> registerUser (@RequestBody UserDetailsInput userDetailsInput) throws Exception {
        try {
            return new ResponseEntity<>(userService.registerUser(userDetailsInput), HttpStatus.CREATED);
        } catch (ValidationException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }
}
