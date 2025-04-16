package com.assurance.assuranceback.Entity.FactureEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class ResponseMessage {
    private String status;
    private String message;
}
