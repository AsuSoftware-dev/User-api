package com.asusoftware.user_api.controller;

import com.asusoftware.user_api.model.RegularUser;
import com.asusoftware.user_api.model.User;
import com.asusoftware.user_api.model.dto.LoginRequest;
import com.asusoftware.user_api.model.dto.UserDto;
import com.asusoftware.user_api.service.KeycloakService;
import com.asusoftware.user_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @PostMapping
    public ResponseEntity<RegularUser> createUser(@RequestPart("user") RegularUser user, @RequestPart("profileImage") MultipartFile profileImage) {
        RegularUser createdUser = userService.createUser(user, profileImage);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", "gateway-client");
        map.add("username", loginRequest.getEmail());
        map.add("password", loginRequest.getPassword());
        map.add("grant_type", "password");
        map.add("client_secret", "6TXH8DVp3EmdFS3SfsLBWTTV8riSV8Vl");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                serverUrl + "/realms/master/protocol/openid-connect/token",
                request,
                String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return ResponseEntity.ok(response.getBody());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<RegularUser>> findAllUsers() {
        return new ResponseEntity<>(userService.findUsers(), HttpStatus.OK);
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
