package com.assurance.assuranceback.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityDTO {
    private List<DateCountDTO> dailyActivity;
    private List<MonthCountDTO> monthlyActivity;
}