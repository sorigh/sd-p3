package com.example.user_service.dto.builders;

import com.example.user_service.dto.UserDTO;
import com.example.user_service.dto.UserDetailsDTO;
import com.example.user_service.entity.User;
import lombok.experimental.UtilityClass;

/**
 * Utility class for converting between User entities and DTOs.
 */
@UtilityClass
public class UserBuilder {

    /**
     * Converts a User entity to a lightweight UserDTO (no password).
     */
    public UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername()) // ✅ added
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    /**
     * Converts a User entity to a detailed UserDetailsDTO (includes username & password).
     */
    public UserDetailsDTO toUserDetailsDTO(User user) {
        return UserDetailsDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .password(user.getPassword())
                .build();
    }

    /**
     * Converts a UserDetailsDTO to a User entity (for insert/update).
     */
    public User toEntity(UserDetailsDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .name(userDTO.getName())
                .role(userDTO.getRole())
                .password(userDTO.getPassword()) // hashed later
                .build();
    }
}
