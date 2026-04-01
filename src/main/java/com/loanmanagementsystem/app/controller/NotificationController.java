package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.NotificationResponse;
import com.loanmanagementsystem.app.entity.enums.NotificationType;
import com.loanmanagementsystem.app.service.NotificationService;
import com.loanmanagementsystem.app.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
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

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificationResponse> responses = notificationService.getNotificationsByUserId(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationResponse> responses = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NotificationResponse>> getMyNotificationById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        NotificationResponse response = notificationService.getNotificationById(id, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(
            @PathVariable Long id,
            @PathVariable Long userId) {
        NotificationResponse response = notificationService.getNotificationById(id, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
