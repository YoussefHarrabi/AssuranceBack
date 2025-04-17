package com.assurance.assuranceback.Entity.chatbot;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder

public class ChatResponse {
    private String message;
    private String timestamp;
    private String type;
    private Object data;

    // Constructeur public
    public ChatResponse(String message, String timestamp, String type, Object data) {
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.data = data;
    }

    // Getters et Setters publics
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}