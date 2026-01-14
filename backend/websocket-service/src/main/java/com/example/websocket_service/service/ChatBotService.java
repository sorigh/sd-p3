package com.example.websocket_service.service;

import com.example.websocket_service.dtos.ChatMessageDTO;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ChatBotService {

    public Optional<String> getBotResponse(String userMessage) {
        String msg = userMessage.toLowerCase();

        // Rule 1: Greetings
        if (msg.contains("hello") || msg.contains("hi")) {
            return Optional.of("Hello! How can I help you today?");
        }
        // Rule 2: Overconsumption/Alerts
        if (msg.contains("alert") || msg.contains("red")) {
            return Optional.of("Alerts are triggered when your hourly consumption exceeds the device limit.");
        }
        // Rule 3: Adding devices
        if (msg.contains("add device") || msg.contains("new meter")) {
            return Optional.of("To add a device, go to the 'Management' section and enter the device details.");
        }
        // Rule 4: System Tech
        if (msg.contains("how does it work") || msg.contains("broker")) {
            return Optional.of("We use RabbitMQ to process device data and WebSockets for real-time alerts.");
        }
        // Rule 5: Password
        if (msg.contains("password") || msg.contains("forgot")) {
            return Optional.of("If you've forgotten your password, Please contact the system administrator to reset credentials.");
        }
        // Rule 6: Support hours
        if (msg.contains("support") || msg.contains("hours")) {
            return Optional.of("Our support team is available 24/7 for assistance.");
        }
        // Rule 7: Goodbye
        if (msg.contains("goodbye") || msg.contains("bye")) {
            return Optional.of("Goodbye! Feel free to come back anytime if you have more questions.");
        }
        // Rule 8: Thank you
        if (msg.contains("thank you") || msg.contains("thanks")) {
            return Optional.of("You're welcome! Happy to help.");
        }
        // Rule 9: Pricing
        if (msg.contains("pricing") || msg.contains("cost")) {
            return Optional.of("Our pricing plans are available on the website. Please visit the 'Pricing' section for details.");
        }
        // Rule 10: Features
        if (msg.contains("features") || msg.contains("capabilities")) {
            return Optional.of("Our system offers real-time monitoring, alert notifications, and detailed consumption reports.");
        }
        // If no rule matches, return empty (this is where AI or Admin forwarding happens later)
        return Optional.empty();
    }
}