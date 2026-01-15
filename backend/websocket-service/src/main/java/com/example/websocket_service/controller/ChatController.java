package com.example.websocket_service.controller;

import com.example.websocket_service.dtos.ChatMessageDTO;
import com.example.websocket_service.service.ChatBotService;
import com.example.websocket_service.service.LlmService;
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
    private final LlmService llmService; // Adaug noul serviciu
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDTO chatMessage) {
        // 1. Încearcă regulile statice
        Optional<String> botResponse = chatBotService.getBotResponse(chatMessage.getContent());
        String finalContent;

        if (botResponse.isPresent()) {
            finalContent = botResponse.get();
        } else {
            finalContent = llmService.generateAIResponse(chatMessage.getContent());
        }

        ChatMessageDTO responseDTO = new ChatMessageDTO(
                "BOT",
                finalContent,
                LocalDateTime.now().toString(),
                true
        );

        messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getSenderId(), responseDTO);
    }
}