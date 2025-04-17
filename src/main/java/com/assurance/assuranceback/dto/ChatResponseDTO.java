package com.assurance.assuranceback.dto;

import lombok.Data;

import java.util.List;

@Data


public class ChatResponseDTO {
    private String message;
    private String timestamp;
    private ResponseType type;
    private ResponseDataDTO data;
    private Object content;
    private String metadata;
    private List<String> suggestedQuestions;

    public List<String> getSuggestedQuestions() {
        return suggestedQuestions;
    }

    public void setSuggestedQuestions(List<String> suggestedQuestions) {
        this.suggestedQuestions = suggestedQuestions;
    }

    public enum ResponseType {
        TEXT,
        LOCATION,
        CONTACT,
        SCHEDULE,
        ERROR
    }

}