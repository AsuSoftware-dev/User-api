package com.asusoftware.user_api.controller;

import com.asusoftware.user_api.model.Place;
import com.asusoftware.user_api.model.dto.PlaceDto;
import com.asusoftware.user_api.service.PlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/places")
public class PlaceController {

    @Autowired
    private PlaceService placeService;

    @PostMapping
    public ResponseEntity<Place> createPlace(@RequestPart("place") Place place, @RequestPart("profileImage") MultipartFile profileImage) {
        Place createdPlace = placeService.createPlace(place, profileImage);
        return new ResponseEntity<>(createdPlace, HttpStatus.CREATED);
    }

    @PutMapping("/{placeId}")
    public ResponseEntity<Place> updatePlace(
            @PathVariable UUID placeId,
            @RequestPart("place") Place updatedPlace,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        Place place = placeService.updatePlace(placeId, updatedPlace, profileImage);
        return new ResponseEntity<>(place, HttpStatus.OK);
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

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceDto> getPlaceById(@PathVariable UUID placeId) {
        PlaceDto placeDTO = placeService.getPlaceByIdWithLocation(placeId);
        return new ResponseEntity<>(placeDTO, HttpStatus.OK);
    }
}
