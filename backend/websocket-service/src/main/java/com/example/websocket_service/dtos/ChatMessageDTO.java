package com.example.websocket_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private String senderId;    // The identifier of the user (required by assignment)
    private String content;     // The actual message text
    private String timestamp;   // Optional, for UI display
    @JsonProperty("isBot")
    private boolean isBot;      // To distinguish between user and bot responses in the UI
    @JsonProperty("isAdmin")
    private boolean isAdmin;
}