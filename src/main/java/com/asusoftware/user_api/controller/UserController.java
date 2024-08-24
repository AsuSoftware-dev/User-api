package com.asusoftware.user_api.controller;

import com.asusoftware.user_api.model.RegularUser;
import com.asusoftware.user_api.model.User;
import com.asusoftware.user_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<RegularUser> createUser(@RequestPart("user") RegularUser user, @RequestPart("profileImage") MultipartFile profileImage) {
        RegularUser createdUser = userService.createUser(user, profileImage);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<RegularUser> updateUser(
            @PathVariable UUID userId,
            @RequestPart("user") RegularUser updatedUser,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        RegularUser user = userService.updateUser(userId, updatedUser, profileImage);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/{userId}/follow/{followeeId}")
    public ResponseEntity<Void> followUser(@PathVariable UUID userId, @PathVariable UUID followeeId) {
        userService.followUser(userId, followeeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/unfollow/{followeeId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable UUID userId, @PathVariable UUID followeeId) {
        userService.unfollowUser(userId, followeeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{userId}/profile-image")
    public ResponseEntity<Void> updateProfileImage(
            @PathVariable UUID userId,
            @RequestPart("profileImage") MultipartFile profileImage) {
        userService.updateProfileImage(userId, profileImage);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
