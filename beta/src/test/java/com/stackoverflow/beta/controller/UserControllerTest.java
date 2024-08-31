package com.stackoverflow.beta.controller;


import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.User;
import com.stackoverflow.beta.model.request.UserDetailsInput;
import com.stackoverflow.beta.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Mock user info
        UserDetailsInput userDetailsInput = mockUserRequest();

        User mockUserResponse = mockUserResponse();

        ResponseEntity<?> responseEntity = ResponseEntity.ok("User registered successfully");
        when(userService.registerUser(any(UserDetailsInput.class))).thenReturn(mockUserResponse);


        ResponseEntity<?> response = userController.registerUser(userDetailsInput);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockUserResponse, response.getBody());
    }

    @Test
    void testRegisterUser_Failure() throws Exception {
        // Mock user info
        UserDetailsInput userDetailsInput = mockUserRequest();

        when(userService.registerUser(any(UserDetailsInput.class))).thenThrow(new ValidationException("User already exist", HttpStatus.BAD_REQUEST));

        ResponseEntity<?> response = userController.registerUser(userDetailsInput);

        // Verify the response
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User already exist", response.getBody());
    }

    private User mockUserResponse() {
        User mockUserResponse = new User();
        mockUserResponse.setEmail("mockuser@example.com");
        mockUserResponse.setName("mockuser");
        mockUserResponse.setId(1);
        return mockUserResponse;
    }

    private UserDetailsInput mockUserRequest() {
        UserDetailsInput userDetailsInput = new UserDetailsInput();
        userDetailsInput.setName("mockuser");
        userDetailsInput.setEmail("mockuser@example.com");
        return userDetailsInput;
    }
}
