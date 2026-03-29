package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.NotificationResponse;
import com.loanmanagementsystem.app.entity.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    void sendNotification(Long userId, NotificationType type, String subject, String message);

    List<NotificationResponse> getNotificationsByUserId(Long userId);

    NotificationResponse getNotificationById(Long id, Long userId);
}
