package com.stackoverflow.beta.controller;
import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.User;
import com.stackoverflow.beta.model.request.UserDetailsInput;
import com.stackoverflow.beta.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user. Accessible by anyone.
     */
    @PostMapping(path = "/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDetailsInput userDetailsInput) {
        log.info("Received registration request for user: {}", userDetailsInput);
        try {
            User user = userService.registerUser(userDetailsInput);
            log.info("User registered successfully: {}", user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (ValidationException e) {
            log.error("Validation error during registration: {}", e.getMessage());
            return ResponseEntity.status(e.getStatus())
                    .body(null);
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
