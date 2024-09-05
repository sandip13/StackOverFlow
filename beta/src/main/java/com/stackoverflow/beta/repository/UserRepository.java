package com.stackoverflow.beta.repository;

import com.stackoverflow.beta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(nativeQuery = true, value = "SELECT * FROM users where email = :email")
    User findByEmail(String email);
}