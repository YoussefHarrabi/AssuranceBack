package com.assurance.assuranceback.Entity.chatbot;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ChatRequest {
    private String message;
    private LocalDateTime timestamp;
    private String userId;
}
