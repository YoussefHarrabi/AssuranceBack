package com.assurance.assuranceback.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleAuthRequest {
    private String code;
    private String redirectUri;
}