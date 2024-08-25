package com.asusoftware.user_api.model.dto;

import com.asusoftware.user_api.client.dto.LocationDto;
import com.asusoftware.user_api.model.ContactPerson;
import com.asusoftware.user_api.model.PlaceType;
import com.asusoftware.user_api.model.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePlaceDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private boolean enabled;
    private Role role;
    private String profileImageUrl;
    private PlaceType placeType;
    private ContactPerson contactPerson;
    private LocationDto location;
}
