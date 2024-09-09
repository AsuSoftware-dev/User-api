package com.asusoftware.user_api.controller;

import com.asusoftware.user_api.model.Place;
import com.asusoftware.user_api.model.dto.CreatePlaceDto;
import com.asusoftware.user_api.model.dto.PlaceDto;
import com.asusoftware.user_api.model.dto.UpdatePlaceDto;
import com.asusoftware.user_api.model.dto.UserDto;
import com.asusoftware.user_api.service.KeycloakService;
import com.asusoftware.user_api.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PlaceDto> createPlace(
            @RequestPart("placeDto") CreatePlaceDto placeDto,
            @RequestPart("profileImage") MultipartFile profileImage) {

        Place place = placeService.convertToEntity(placeDto);
        Place createdPlace = placeService.createPlace(place, profileImage, placeDto.getLocation());
        return ResponseEntity.status(HttpStatus.CREATED).body(placeService.convertToDto(createdPlace));
    }


    @PutMapping("/{placeId}")
    public ResponseEntity<PlaceDto> updatePlace(@PathVariable UUID placeId,
                                                @RequestBody UpdatePlaceDto placeDto,
                                                @RequestParam("profileImage") MultipartFile profileImage) {
        Place updatedPlace = placeService.updatePlace(placeId, placeService.convertToEntity(placeDto), profileImage, placeDto.getLocation());
        return ResponseEntity.ok(placeService.convertToDto(updatedPlace));
    }

    @PostMapping("/{userId}/follow/{placeId}")
    public ResponseEntity<Void> followPlace(@PathVariable UUID userId, @PathVariable UUID placeId) {
        placeService.followPlace(userId, placeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/unfollow/{placeId}")
    public ResponseEntity<Void> unfollowPlace(@PathVariable UUID userId, @PathVariable UUID placeId) {
        placeService.unfollowPlace(userId, placeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{placeId}")
    public ResponseEntity<Void> deletePlace(@PathVariable UUID placeId) {
        placeService.deletePlace(placeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceDto> getPlaceById(@PathVariable UUID placeId) {
        PlaceDto placeDTO = placeService.getPlaceByIdWithLocation(placeId);
        return new ResponseEntity<>(placeDTO, HttpStatus.OK);
    }
}
