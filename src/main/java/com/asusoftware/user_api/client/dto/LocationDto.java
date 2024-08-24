package com.asusoftware.user_api.client.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class LocationDto {

    private UUID id;
    private String city;
    private String state;
    private String country;
}
