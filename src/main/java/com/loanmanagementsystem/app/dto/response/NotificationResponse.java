package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private Long userId;
    private NotificationType type;
    private String subject;
    private String message;
    private String emailId;
    private LocalDateTime sentAt;
}
