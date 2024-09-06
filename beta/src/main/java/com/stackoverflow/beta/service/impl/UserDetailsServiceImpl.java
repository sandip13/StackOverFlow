package com.stackoverflow.beta.service.impl;


import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.Role;
import com.stackoverflow.beta.model.User;
import com.stackoverflow.beta.model.request.UserDetailsInput;
import com.stackoverflow.beta.repository.RoleRepository;
import com.stackoverflow.beta.repository.UserRepository;
import com.stackoverflow.beta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(UserDetailsInput userDetailsInput) throws ValidationException {
        validateUserEmail(userDetailsInput.getEmail());
        User user = createUser(userDetailsInput);
        return userRepository.save(user);
    }

    private void validateUserEmail(String email) throws ValidationException {
        if (userRepository.findByEmail(email) != null) {
            throw new ValidationException("User with this email already exists.");
        }
    }

    private User createUser(UserDetailsInput userDetailsInput) {
        // Fetch or create the WRITER role
        Role writerRole = roleRepository.findByName("WRITER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("WRITER").build()));


        return User.builder()
                .name(userDetailsInput.getName())
                .email(userDetailsInput.getEmail())
                .password(passwordEncoder.encode(userDetailsInput.getPassword()))
                .roles(Set.of(writerRole))
                .build();
    }

    @Override
    public boolean isUserExist(int userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public Optional<User> getUserById(int userId) {
        return userRepository.findById(userId);
    }

    private Set<Role> getRolesFromInput(List<String> rolesInput) throws ValidationException {
        List<Role> allRoles = roleRepository.findAll();
        return rolesInput.stream()
                .map(roleInput -> findRoleByName(allRoles, roleInput))
                .collect(Collectors.toSet());
    }

    private Role findRoleByName(List<Role> allRoles, String roleName) throws ValidationException {
        return allRoles.stream()
                .filter(role -> role.getName().equalsIgnoreCase(roleName))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Role does not exist: " + roleName));
    }
}