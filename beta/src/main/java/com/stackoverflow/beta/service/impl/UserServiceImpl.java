package com.stackoverflow.beta.service.impl;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.User;
import com.stackoverflow.beta.model.request.UserDetailsInput;
import com.stackoverflow.beta.repository.UserRepository;
import com.stackoverflow.beta.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository =userRepository;
    }

    public User registerUser(UserDetailsInput userDetails) throws Exception {
        if (emailExist(userDetails.getEmail())) {
            log.error("User with same email address already exists");
            throw new ValidationException
                    ("Account already exists with email address: " + userDetails.getEmail(), HttpStatus.BAD_REQUEST);
        }
        try{
            User user = User.builder()
                    .name(userDetails.getName())
                    .email(userDetails.getEmail())
                    .build();
            // Save the new user to the repository
            User savedUser =  userRepository.save(user);
            log.info("User created successfully");
            return savedUser;
        } catch (Exception e) {
            log.error("Error occurred while creating user");
            throw new Exception("Error occurred while creating user");
        }

    }

    @Override
    public boolean isUserExist(int userId) {
        return userRepository.existsById(userId);
    }

    private boolean emailExist(String email){
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public Optional<User> getUserById(int userId){
        return userRepository.findById(userId);
    }
}
