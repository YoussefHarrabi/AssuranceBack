package com.assurance.assuranceback.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyStatusDTO {

    private Long id;

    @NotBlank(message = "Tier name is required")
    private String tier;

    @NotNull(message = "Points threshold is required")
    @Positive(message = "Points threshold must be positive")
    private Integer pointsThreshold;

    private String description;

    private String benefits;
}