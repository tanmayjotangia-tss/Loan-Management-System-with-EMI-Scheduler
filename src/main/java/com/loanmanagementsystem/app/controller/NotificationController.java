package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.NotificationResponse;
import com.loanmanagementsystem.app.service.NotificationService;
import com.loanmanagementsystem.app.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<NotificationResponse> responses =
                notificationService.getNotificationsByUserId(userDetails.getUserId());

        return ResponseEntity.ok(
                ApiResponse.success(200, "Notifications fetched successfully", responses)
        );
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByUserId(
            @PathVariable Long userId) {

        List<NotificationResponse> responses =
                notificationService.getNotificationsByUserId(userId);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Notifications fetched successfully for user", responses)
        );
    }

    @GetMapping("/{id}/me")
    @PreAuthorize("hasRole('BORROWER')")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        NotificationResponse response =
                notificationService.getNotificationById(id, userDetails.getUserId());

        return ResponseEntity.ok(
                ApiResponse.success(200, "Notification fetched successfully", response)
        );
    }

    @GetMapping("/{id}/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LOAN_OFFICER')")
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(
            @PathVariable Long id,
            @PathVariable Long userId) {

        NotificationResponse response =
                notificationService.getNotificationById(id, userId);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Notification fetched successfully for user", response)
        );
    }
}