package com.asusoftware.user_api.service;

import com.asusoftware.user_api.client.NotificationClient;
import com.asusoftware.user_api.exception.UserCreationException;
import com.asusoftware.user_api.model.RegularUser;
import com.asusoftware.user_api.model.dto.NotificationRequest;
import com.asusoftware.user_api.model.dto.NotificationRequestType;
import com.asusoftware.user_api.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaceService placeService; // Pentru interacțiunea cu locurile

    @Autowired
    private ImageService imageService;

    @Autowired
    private NotificationClient notificationClient; // Pentru trimiterea notificărilor

    public RegularUser createUser(RegularUser user, MultipartFile profileImage) {
        try {
            String imageUrl = imageService.uploadImage(profileImage, user.getId());
            user.setProfileImageUrl(imageUrl);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new UserCreationException("Failed to create user", e);
        }
    }

    public RegularUser updateUser(UUID userId, RegularUser updatedUser, MultipartFile profileImage) {
        RegularUser user = findUserById(userId);

        if (profileImage != null && !profileImage.isEmpty()) {
            String imageUrl = imageService.uploadImage(profileImage, userId);
            user.setProfileImageUrl(imageUrl);
        }

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setSubscriptionType(updatedUser.getSubscriptionType());

        return userRepository.save(user);
    }

    public void updateProfileImage(UUID userId, MultipartFile profileImage) {
        RegularUser user = findUserById(userId);

        String imageUrl = imageService.uploadImage(profileImage, userId);
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
    }

    public void followUser(UUID followerId, UUID followeeId) {
        RegularUser follower = findUserById(followerId);
        RegularUser followee = findUserById(followeeId);

        follower.getFollowing().add(followee);
        userRepository.save(follower);

        notificationClient.sendNotification(buildFollowNotification(followerId, followeeId));
    }

    public void unfollowUser(UUID followerId, UUID followeeId) {
        RegularUser follower = findUserById(followerId);
        RegularUser followee = findUserById(followeeId);

        follower.getFollowing().remove(followee);
        userRepository.save(follower);
    }

    private RegularUser findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    private NotificationRequest buildFollowNotification(UUID followerId, UUID followeeId) {
        String followerName = findUserById(followerId).getFirstName();
        String followeeName = findUserById(followeeId).getFirstName();
        String message = String.format("%s a început să te urmărească.", followerName);

        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setRecipientId(followeeId);
        notificationRequest.setSenderId(followerId);
        notificationRequest.setNotificationType(NotificationRequestType.FOLLOW);
        notificationRequest.setMessage(message);

        return notificationRequest;
    }
}

