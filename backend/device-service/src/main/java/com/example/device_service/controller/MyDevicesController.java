package com.example.device_service.controller;

import com.example.device_service.dto.DeviceDTO;
import com.example.device_service.service.DeviceService;
import com.example.device_service.handlers.exceptions.ResourceNotFoundException;
import io.jsonwebtoken.Claims;
import com.example.device_service.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/my-devices")
@CrossOrigin(origins = "http://localhost:3000")
public class MyDevicesController {

    private final DeviceService deviceService;
    private final JwtUtil jwtUtil;

    public MyDevicesController(DeviceService deviceService, JwtUtil jwtUtil) {
        this.deviceService = deviceService;
        this.jwtUtil = jwtUtil;
    }

    // Get devices for current user
    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getMyDevices(@RequestHeader("Authorization") String token) {
        // Extract user ID from JWT token
        Long userId = extractUserIdFromToken(token);
        if (userId == null) {
            throw new ResourceNotFoundException("Invalid token or user not found");
        }

        List<DeviceDTO> devices = deviceService.findByOwnerId(userId);
        return ResponseEntity.ok(devices);
    }

    private Long extractUserIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            try {
                // Use JwtUtil to validate and extract claims
                Claims claims = jwtUtil.validateToken(jwt);
                
                // Get userId from claims (not from "sub" which is username)
                Object userIdObj = claims.get("userId");
                
                if (userIdObj instanceof Integer) {
                    return ((Integer) userIdObj).longValue();
                } else if (userIdObj instanceof Long) {
                    return (Long) userIdObj;
                } else {
                    System.err.println("userId claim is not a number: " + userIdObj);
                    return null;
                }
            } catch (Exception e) {
                System.err.println("Failed to extract userId from token: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}