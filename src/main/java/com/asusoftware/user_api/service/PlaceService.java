package com.asusoftware.user_api.service;

import com.asusoftware.user_api.client.LocationClient;
import com.asusoftware.user_api.client.NotificationClient;
import com.asusoftware.user_api.client.dto.LocationDto;
import com.asusoftware.user_api.exception.*;
import com.asusoftware.user_api.model.Place;
import com.asusoftware.user_api.model.RegularUser;
import com.asusoftware.user_api.model.Role;
import com.asusoftware.user_api.model.dto.*;
import com.asusoftware.user_api.repository.PlaceRepository;
import com.asusoftware.user_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
public class PlaceService {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private LocationClient locationServiceClient;

    @Autowired
    private NotificationClient notificationClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeycloakService keycloakService;

    @Transactional  // Asigură că toate operațiunile sunt într-o singură tranzacție
    public Place createPlace(Place place, MultipartFile profileImage, LocationDto locationDto) {
        try {
            // Create location in location-api and get the location ID
            LocationDto createdLocation = locationServiceClient.createLocation(locationDto);
            place.setLocationId(createdLocation.getId());

            // Upload the image and set the image URL
            String imageUrl = imageService.uploadImage(profileImage, place.getId());
            place.setProfileImageUrl(imageUrl);

            // Salvează Place-ul în baza de date
            Place savedPlace = placeRepository.save(place);

            // Sincronizează Place-ul cu Keycloak
            keycloakService.createUserInKeycloak(UserDto.toDto(savedPlace));

            return savedPlace;

        } catch (Exception e) {
            throw new PlaceCreationException("Failed to create place. Reason: " + e.getMessage(), e);
        }
    }


    public Place updatePlace(UUID placeId, Place updatedPlace, MultipartFile profileImage, LocationDto locationDto) {
        try {
            Place place = placeRepository.findById(placeId)
                    .orElseThrow(() -> new EntityNotFoundException("Place not found"));

            if (profileImage != null && !profileImage.isEmpty()) {
                String imageUrl = imageService.uploadImage(profileImage, placeId);
                place.setProfileImageUrl(imageUrl);
            }

            if(locationDto != null) {
                // Update location in location-api
                LocationDto updatedLocation = locationServiceClient.updateLocation(place.getLocationId(), locationDto);
                place.setLocationId(updatedLocation.getId());
            }

            place.setFirstName(updatedPlace.getFirstName());
            place.setLastName(updatedPlace.getLastName());
            place.setEmail(updatedPlace.getEmail());
            place.setContactPerson(updatedPlace.getContactPerson());
            place.setPlaceType(updatedPlace.getPlaceType());

            return placeRepository.save(place);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PlaceUpdateException("Failed to update place with ID " + placeId, e);
        }
    }

    public void deletePlace(UUID placeId) {
        try {
            Place place = placeRepository.findById(placeId)
                    .orElseThrow(() -> new EntityNotFoundException("Place not found"));

            // Delete location in location-api
            locationServiceClient.deleteLocation(place.getLocationId());

            placeRepository.delete(place);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PlaceDeletionException("Failed to delete place with ID " + placeId, e);
        }
    }


    public void followPlace(UUID userId, UUID placeId) {
        try {
            RegularUser user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            Place place = placeRepository.findById(placeId)
                    .orElseThrow(() -> new EntityNotFoundException("Place not found"));

            user.getFollowing().add(place);
            userRepository.save(user);

            // Build the NotificationRequest DTO
            NotificationRequest notificationRequest = buildNotificationRequest(userId, placeId, NotificationRequestType.FOLLOW);

            // Send the notification
            notificationClient.sendNotification(notificationRequest);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new FollowOperationException("Failed to follow place with ID " + placeId + " by user " + userId, e);
        }
    }

    private NotificationRequest buildNotificationRequest(UUID userId, UUID placeId, NotificationRequestType notificationRequestType) {
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setRecipientId(placeId); // Assuming placeId is the recipient
        notificationRequest.setSenderId(userId);
        notificationRequest.setNotificationType(notificationRequestType);
        notificationRequest.setMessage(String.format("User %s has started following place %s.", getUserName(userId), getPlaceName(placeId)));
        notificationRequest.setRelatedPlaceId(placeId);

        return notificationRequest;
    }

    private String getUserName(UUID userId) {
        // Logic to retrieve the user name
        return userRepository.findById(userId)
                .map(user -> user.getFirstName() + " " + user.getLastName())
                .orElse("Unknown User");
    }

    private String getPlaceName(UUID placeId) {
        // Logic to retrieve the place name
        return placeRepository.findById(placeId)
                .map(place -> place.getFirstName() + " " + place.getLastName())
                .orElse("Unknown Place");
    }


    public void unfollowPlace(UUID userId, UUID placeId) {
        try {
            RegularUser user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            Place place = placeRepository.findById(placeId)
                    .orElseThrow(() -> new EntityNotFoundException("Place not found"));

            user.getFollowing().remove(place);
            userRepository.save(user);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UnfollowOperationException("Failed to unfollow place with ID " + placeId + " by user " + userId, e);
        }
    }

    public PlaceDto getPlaceById(UUID placeId) {
        try {
            Place place = placeRepository.findById(placeId)
                    .orElseThrow(() -> new EntityNotFoundException("Place not found"));

            // Preluăm locația folosind ID-ul locației
            return getPlaceDto(place);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PlaceRetrievalException("Failed to retrieve place with ID " + placeId, e);
        }
    }

    public Place getPlaceEntityById(UUID placeId) {
        try {
            return placeRepository.findById(placeId)
                    .orElseThrow(() -> new EntityNotFoundException("Place not found"));
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PlaceRetrievalException("Failed to retrieve place with ID " + placeId, e);
        }
    }

    public PlaceDto getPlaceByIdWithLocation(UUID placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new EntityNotFoundException("Place not found with ID: " + placeId));

        return getPlaceDto(place);
    }

    private PlaceDto getPlaceDto(Place place) {
        LocationDto locationDTO = locationServiceClient.getLocationById(place.getLocationId());
        PlaceDto placeDto = new PlaceDto();
        placeDto.setId(place.getId());
        placeDto.setFirstName(place.getFirstName());
        placeDto.setLastName(place.getLastName());
        placeDto.setProfileImageUrl(place.getProfileImageUrl());
        placeDto.setPlaceType(place.getPlaceType());
        placeDto.setContactPerson(place.getContactPerson());
        placeDto.setLocation(locationDTO);

        return placeDto;
    }


    //////////////

    public Place convertToEntity(CreatePlaceDto placeDto) {
        Place place = new Place();
        place.setFirstName(placeDto.getFirstName());
        place.setLastName(placeDto.getLastName());
        place.setEmail(placeDto.getEmail());
        place.setPassword(placeDto.getPassword()); // TODO: cehck keycloak for this
        place.setEnabled(placeDto.isEnabled());
        place.setRole(Role.PLACE); // Set the role as "PLACE"
        place.setPlaceType(placeDto.getPlaceType());
        place.setContactPerson(placeDto.getContactPerson());
        place.setLocationId(placeDto.getLocation().getId());
        return place;
    }

    public Place convertToEntity(UpdatePlaceDto placeDto) {
        Place place = new Place();
        place.setFirstName(placeDto.getFirstName());
        place.setLastName(placeDto.getLastName());
        place.setEmail(placeDto.getEmail());
        place.setPassword(placeDto.getPassword()); // TODO: cehck keycloak for this
        place.setEnabled(placeDto.isEnabled());
        place.setRole(Role.PLACE); // Set the role as "PLACE"
        place.setPlaceType(placeDto.getPlaceType());
        place.setContactPerson(placeDto.getContactPerson());
        place.setLocationId(placeDto.getLocation().getId());
        return place;
    }

    public PlaceDto convertToDto(Place place) {
        PlaceDto placeDto = new PlaceDto();
        placeDto.setId(place.getId());
        placeDto.setFirstName(place.getFirstName());
        placeDto.setLastName(place.getLastName());
        placeDto.setProfileImageUrl(place.getProfileImageUrl());
        placeDto.setPlaceType(place.getPlaceType());
        placeDto.setContactPerson(place.getContactPerson());
        placeDto.setLocation(locationServiceClient.getLocationById(place.getLocationId()));
        return placeDto;
    }

    public PlaceDto convertPlaceDtoToDto(Place place) {
        PlaceDto placeDto = new PlaceDto();
        placeDto.setId(place.getId());
        placeDto.setFirstName(place.getFirstName());
        placeDto.setLastName(place.getLastName());
        placeDto.setProfileImageUrl(place.getProfileImageUrl());
        placeDto.setPlaceType(place.getPlaceType());
        placeDto.setContactPerson(place.getContactPerson());
        return placeDto;
    }

    public List<PlaceDto> findPlacesByLocationIds(List<UUID> locationIds) {
        List<PlaceDto> placeDtos = placeRepository.findPlacesByLocationIds(locationIds).stream().map(place -> convertPlaceDtoToDto(place)).collect(Collectors.toList());
        return placeDtos;
    }
}

