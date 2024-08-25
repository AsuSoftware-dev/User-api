package com.asusoftware.user_api.client.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class LocationDto {
    private UUID id;
    private String street;
    private String number;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private double latitude;
    private double longitude;
}

