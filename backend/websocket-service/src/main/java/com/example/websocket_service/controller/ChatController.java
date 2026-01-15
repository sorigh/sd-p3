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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatBotService chatBotService;
    private final LlmService llmService; // Adaug noul serviciu
    private final SimpMessagingTemplate messagingTemplate;

    private final Set<String> adminHandledChats = ConcurrentHashMap.newKeySet();


    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessageDTO chatMessage) {
        // 1. Încearcă regulile statice
        messagingTemplate.convertAndSend("/topic/admin/messages", chatMessage);

        if (adminHandledChats.contains(chatMessage.getSenderId())) {
            return; // so admin and llm dont respond at the same time
        }

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
                true,
                false
        );

        messagingTemplate.convertAndSend("/topic/chat/" + chatMessage.getSenderId(), responseDTO);

        //for admin
        messagingTemplate.convertAndSend("/topic/admin/messages", responseDTO);
    }


    //new endpoint
    @MessageMapping("/admin/reply")
    public void adminReply(@Payload ChatMessageDTO adminMessage) {
        adminHandledChats.add(adminMessage.getSenderId());
        // Setăm flag-urile corect pentru admin
        adminMessage.setBot(false);
        adminMessage.setAdmin(true);
        adminMessage.setTimestamp(LocalDateTime.now().toString());
        

        // Trimitem răspunsul adminului către user (pe topicul lui privat)
        messagingTemplate.convertAndSend("/topic/chat/" + adminMessage.getSenderId(), adminMessage);
        
        // Trimitem și către topicul de admini pentru a sincroniza toate ferestrele de dashboard
        messagingTemplate.convertAndSend("/topic/admin/messages", adminMessage);
    }

    
    
}