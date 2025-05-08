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
public class BonusDTO {

    private Long id;

    @NotBlank(message = "Bonus name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Points required is required")
    @Positive(message = "Points required must be positive")
    private Integer pointsRequired;

    private String imageUrl;

    @NotNull(message = "Availability status is required")
    private boolean isAvailable;

    @NotNull(message = "Stock is required")
    @Positive(message = "Stock must be positive")
    private Integer stock;
}