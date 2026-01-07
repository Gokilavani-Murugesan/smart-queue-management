package com.smartqueue.repository;

import com.smartqueue.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);       // checks if email already exists
    User findByEmail(String email);            // fetch user by email for login
}
