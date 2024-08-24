package com.asusoftware.user_api.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class NotificationRequest {

    private UUID recipientId;
    private UUID senderId;
    private NotificationRequestType notificationType;
    private String message;
    private UUID relatedPostId;
    private UUID relatedPlaceId;
}
