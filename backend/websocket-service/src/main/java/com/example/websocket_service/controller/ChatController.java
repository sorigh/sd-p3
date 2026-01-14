package com.example.websocket_service.controller;

import com.example.websocket_service.dtos.ChatMessageDTO;
import com.example.websocket_service.service.ChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatBotService chatBotService;
    private final SimpMessagingTemplate messagingTemplate;

    // This handles messages sent to "/app/chat"
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDTO chatMessage) {
        
        // 1. Get the bot's response based on your rules
        Optional<String> response = chatBotService.getBotResponse(chatMessage.getContent());

        if (response.isPresent()) {
            // 2. Create the response DTO
            ChatMessageDTO botResponse = new ChatMessageDTO(
                "BOT", // The sender is the system
                response.get(),
                LocalDateTime.now().toString(),
                true // isBot flag set to true
            );

            // 3. Send it back to the specific user's topic
            // The frontend will subscribe to /topic/chat/{userId}
            String destination = "/topic/chat/" + chatMessage.getSenderId();
            messagingTemplate.convertAndSend(destination, botResponse);
        } else {
            ChatMessageDTO fallbackResponse = new ChatMessageDTO(
            "BOT",
            "I'm not sure about that. I am forwarding your message to our support team...",
            LocalDateTime.now().toString(),
            true
            );
            messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getSenderId(), fallbackResponse);
            // 2. Forward to Admin / AI
            forwardToSupport(chatMessage);
        }
    }
    private void forwardToSupport(ChatMessageDTO chatMessage) {
        // Placeholder for forwarding logic
        // In a real system, this could send the message to an admin dashboard or AI service
        System.out.println("Forwarding message to support: " + chatMessage.getContent());
    }
}