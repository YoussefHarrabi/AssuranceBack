package com.assurance.assuranceback.dto;

import lombok.Data;

@Data
public class ContactDTO {
    private String phone;
    private String email;
    private String fax;
    private SocialMediaDTO socialMedia;
}