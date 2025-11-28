package com.example.auth_service.services;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.auth_service.repositories.AuthRepository;
import com.example.auth_service.requests.LoginRequest;
import com.example.auth_service.requests.RegisterRequest;
import com.example.auth_service.entities.User;
import com.example.auth_service.entities.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository userRepository; // Injected repository
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public Long registerUser(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        // Create new user
        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(encoder.encode(request.password()))
                .role(UserRole.USER)
                .build();

        // Save user
        if (user == null) {
            throw new RuntimeException("User creation failed");
        }
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }

    public String login(LoginRequest loginRequest) {
        // Find the user by username or throw an exception
        var user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new RuntimeException("Username not found"));

        // Check if password matches
        if (!encoder.matches(loginRequest.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // generate JWT access token
        return jwt.generateAccess(
                user.getUsername(),
                String.valueOf(user.getRole()),
                user.getId());
    }

    public Map<String, Object> validateToken(String token) {
        try {
            String username = jwt.extractUsername(token);
            boolean valid = jwt.isTokenValid(token, username);
            return Map.of("username", username, "valid", valid);
        } catch (Exception e) {
            return Map.of("valid", false, "error", e.getMessage());
        }
    }

}
