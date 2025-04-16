package com.assurance.assuranceback.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuth2AuthResponse {
    private String accessToken;
    private String tokenType;
    private boolean requiresMfa;
    private Long userId;
    private String email;
}