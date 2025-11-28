package com.example.auth_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.auth_service.entities.User;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    // Repository methods
    Optional<User> findByUsername(String username);
}