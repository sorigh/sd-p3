package com.example.user_service.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.user_service.entity.User;
import com.example.user_service.entity.UserRole;
import com.example.user_service.repository.UserRepository;

// command line runner runs some code at application startup
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {
            String adminUsername = "admin";

            // check if admin exists and continue as normal or create one at startup
            if (userRepository.findByUsername(adminUsername).isEmpty()) {
                User admin = User.builder()
                        .username(adminUsername)
                        .name("Administrator")
                        .email("admin@yahoo.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(UserRole.ADMIN)
                        .build();
                if (admin != null)
                    userRepository.save(admin);
                else 
                    throw new IllegalStateException("Error creating admin user");
            }
        };
    }
}
