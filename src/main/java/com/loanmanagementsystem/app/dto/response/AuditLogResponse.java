package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.AuditAction;
import com.loanmanagementsystem.app.entity.enums.EntityType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Long id;
    private EntityType entityType;
    private Long entityId;
    private AuditAction action;
    private String oldValue;
    private String newValue;
    private Long performedByUserId;
    private String performedByUserName;
    private LocalDateTime timestamp;
}
