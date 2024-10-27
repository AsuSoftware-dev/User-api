package com.asusoftware.user_api.model.dto;

import com.asusoftware.user_api.client.dto.LocationDto;
import com.asusoftware.user_api.model.ContactPerson;
import com.asusoftware.user_api.model.PlaceType;
import lombok.Data;

import java.util.UUID;

@Data
public class PlaceDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private PlaceType placeType;
    private ContactPerson contactPerson;
    private LocationDto location;  // DTO pentru locație
}
