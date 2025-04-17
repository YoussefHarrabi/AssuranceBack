package com.assurance.assuranceback.dto;


import lombok.Data;

@Data
public class LocationDTO {
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private CoordinatesDTO coordinates;
    private String directions;
}