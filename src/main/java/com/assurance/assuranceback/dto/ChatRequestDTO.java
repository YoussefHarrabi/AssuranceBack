package com.assurance.assuranceback.dto;

import lombok.Data;

@Data
public class ChatRequestDTO {
    private String message;
    private String userId;
    private String timestamp;
}