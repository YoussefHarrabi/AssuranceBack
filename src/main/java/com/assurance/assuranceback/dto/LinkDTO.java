package com.assurance.assuranceback.dto;

import lombok.Data;

@Data
public class LinkDTO {
    private String text;
    private String url;
    private String type; // "internal" ou "external"
}