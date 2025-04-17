package com.assurance.assuranceback.dto;

import lombok.Data;

import java.util.List;

@Data
    public class ResponseDataDTO {
        private LocationDTO location;
        private ContactDTO contact;
        private ScheduleDTO schedule;
        private List<LinkDTO> links;
    private Object content;
    private String metadata;
    }
