package com.asusoftware.user_api.client;

import com.asusoftware.user_api.model.dto.NotificationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/notifications")
    void sendNotification(NotificationRequest notificationRequest);
}
