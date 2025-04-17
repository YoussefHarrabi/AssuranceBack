package com.assurance.assuranceback.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ReviewDTO {
    private String comment;
    private int rating;
    private Set<String> categories = new HashSet<>();
    private Set<String> attachments = new HashSet<>();
    private Long complaintId; // Optionnel, si l'avis est lié à une réclamation
}