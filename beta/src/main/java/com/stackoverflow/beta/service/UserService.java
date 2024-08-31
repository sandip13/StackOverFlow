package com.stackoverflow.beta.service;

import com.stackoverflow.beta.model.User;
import com.stackoverflow.beta.model.request.UserDetailsInput;

import java.util.Optional;

/**
 * Service class for managing user.
 */
public interface UserService {
     User registerUser(UserDetailsInput userDetailsInput) throws Exception;

    /**
     * Checks if a user exists by their ID.
     *
     * @param userId the ID of the user to check
     * @return true if the user exists, false otherwise
     */
     boolean isUserExist(int userId);

     Optional<User> getUserById(int userId);
}
