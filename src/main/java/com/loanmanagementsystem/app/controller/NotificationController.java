package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.NotificationResponse;
import com.loanmanagementsystem.app.entity.enums.NotificationType;
import com.loanmanagementsystem.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<String>> sendNotification(
            @PathVariable Long userId,
            @RequestParam NotificationType type,
            @RequestBody Map<String, String> request) {
        
        String subject = request.get("subject");
        String message = request.get("message");
        
        notificationService.sendNotification(userId, type, subject, message);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Notification sent successfully", null));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationResponse> responses = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}/user/{userId}")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(
            @PathVariable Long id, 
            @PathVariable Long userId) {
        NotificationResponse response = notificationService.getNotificationById(id, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
