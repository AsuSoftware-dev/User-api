package com.asusoftware.user_api.model.dto;

import com.asusoftware.user_api.model.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDto {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private Role role;  // Rolurile definite: USER, PLACE, ADMIN
    private SubscriptionType subscriptionType;  // Numai pentru RegularUser
    private UUID locationId;  // Numai pentru Place
    private PlaceType placeType;  // Numai pentru Place
    private String profileImageUrl;
    private ContactPerson contactPerson;  // Numai pentru Place

    public static UserDto toDto(RegularUser user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPassword(user.getPassword());
        userDto.setEnabled(user.isEnabled());
        userDto.setRole(user.getRole());
        userDto.setSubscriptionType(user.getSubscriptionType());
        return userDto;
    }

    public static UserDto toDto(Place place) {
        UserDto userDto = new UserDto();
        userDto.setEmail(place.getEmail());
        userDto.setUsername(place.getEmail());
        userDto.setEmail(place.getEmail());
        userDto.setUsername(place.getEmail());
        userDto.setPassword(place.getPassword());
        userDto.setEnabled(place.isEnabled());
        userDto.setRole(place.getRole());
        return userDto;
    }
}
