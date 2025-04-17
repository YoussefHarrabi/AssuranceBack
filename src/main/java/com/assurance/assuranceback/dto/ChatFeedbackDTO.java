package com.assurance.assuranceback.dto;

import lombok.Data;

@Data
public class ChatFeedbackDTO {
    private Long messageId;
    private boolean helpful;
    private String comment;
    private String userId;
}