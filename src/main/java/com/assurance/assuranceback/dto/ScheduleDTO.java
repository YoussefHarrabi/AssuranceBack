package com.assurance.assuranceback.dto;

import lombok.Data;

import java.util.List;

@Data
public class ScheduleDTO {
    private WorkingHoursDTO weekdays;
    private WorkingHoursDTO weekend;
    private List<String> holidays;
}