package com.stackoverflow.beta.service;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.User;
import com.stackoverflow.beta.model.request.UserDetailsInput;
import com.stackoverflow.beta.repository.UserRepository;
import com.stackoverflow.beta.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        UserDetailsInput userDetailsInput = getUserInfo();

        when(userRepository.findByEmail("test.user@gmail.com")).thenReturn(null);

        User user = new User(1, "Test User", "test.user@gmail.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.registerUser(userDetailsInput);
        assertNotNull(result);
        assertEquals("Test User", result.getName());
    }

    @Test
    void testRegisterUser_Failure() {
        UserDetailsInput userDetailsInput = getUserInfo();
        when(userRepository.findByEmail("test.user@gmail.com")).thenReturn(new User(1, "Test User", "test.user@gmail.com"));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            userService.registerUser(userDetailsInput);
        });

        assertEquals("Account already exists with email address: test.user@gmail.com", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testGetUserById() {
        Optional<User> expectedUser = Optional.of(new User(1, "Test User", "test.user@gmail.com"));
        when(userRepository.findById(1)).thenReturn(expectedUser);

        Optional<User> actualUser = userService.getUserById(1);
        assertTrue(actualUser.isPresent());
        assertEquals("Test User", actualUser.get().getName());
    }
    private UserDetailsInput getUserInfo() {
        UserDetailsInput userDetailsInput = new UserDetailsInput("Test User", "test.user@gmail.com");
        return userDetailsInput;
    }

}
