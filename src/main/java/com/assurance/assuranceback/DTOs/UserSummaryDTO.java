package com.assurance.assuranceback.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String createdAt;
    private Set<String> roles;
    private boolean mfaEnabled;
}
