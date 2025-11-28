package com.example.user_service.service;

import com.example.user_service.dto.UserDTO;
import com.example.user_service.dto.UserDetailsDTO;
import com.example.user_service.dto.builders.UserBuilder;
import com.example.user_service.entity.User;
import com.example.user_service.entity.UserRole;
import com.example.user_service.handlers.exceptions.model.ResourceNotFoundException;
import com.example.user_service.messaging.UserSyncPublisher;
import com.example.user_service.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserSyncPublisher userSyncPublisher;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserSyncPublisher userSyncPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSyncPublisher = userSyncPublisher;
    }

    public List<UserDTO> findUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDetailsDTO findUserById(Long id) {
        if (id == null) {
            LOGGER.error("Provided id is null");
            throw new IllegalArgumentException("ID cannot be null");
        }
        return userRepository.findById(id)
                .map(UserBuilder::toUserDetailsDTO)
                .orElseThrow(() -> {
                    LOGGER.error("User with id {} was not found in DB", id);
                    return new ResourceNotFoundException("User with id: " + id);
                });
    }

    public Long getUserIdByUsername(String username) {
    System.out.println("Searching for user with username: " + username);
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    return user.getId();
    }

    public Long insert(UserDetailsDTO userDTO) {
        User user = UserBuilder.toEntity(userDTO);

        // Hash the password here
        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            throw new IllegalArgumentException("Password cannot be null");
        }

        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }

        user = userRepository.save(user);
        LOGGER.debug("User with id {} was inserted into DB", user.getId());
        
        // PUBLISH: create user event
        userSyncPublisher.publishUserCreated(user);
        
        return user.getId();
    }


    public Long update(Long id, UserDetailsDTO userDTO) {
    if (id == null) {
        LOGGER.error("Provided id is null for update");
        throw new IllegalArgumentException("ID cannot be null");
    }
    User user = userRepository.findById(id)
            .orElseThrow(() -> {
                LOGGER.error("User with id {} not found for update", id);
                return new ResourceNotFoundException("User with id: " + id);
            });

    // Update fields
    user.setUsername(userDTO.getUsername());
    user.setName(userDTO.getName());
    user.setEmail(userDTO.getEmail());
    if (userDTO.getRole() != null) {
        user.setRole(userDTO.getRole());
    }

    // Only update password if provided
    if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    }

    user = userRepository.save(user);
    LOGGER.debug("User with id {} was updated", user.getId());
    return user.getId();
}


    public void delete(Long id) {
        if (id == null) {
            LOGGER.error("Provided id is null for deletion");
            throw new IllegalArgumentException("ID cannot be null");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("User with id {} not found for deletion", id);
                    return new ResourceNotFoundException("User with id: " + id);
                });
                
        if (user.getRole() == UserRole.ADMIN) {
            LOGGER.error("Attempted to delete admin user with id {}", id);
            throw new IllegalArgumentException("Cannot delete admin users");
        }
        userRepository.delete(user);
        userSyncPublisher.publishUserDeleted(id);
        LOGGER.debug("User with id {} was deleted", id);
    }
}
