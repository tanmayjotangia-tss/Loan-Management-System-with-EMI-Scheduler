package com.loanmanagementsystem.app.service;

import java.util.List;

import com.loanmanagementsystem.app.dto.response.AuditLogResponse;
import com.loanmanagementsystem.app.entity.enums.AuditAction;
import com.loanmanagementsystem.app.entity.enums.EntityType;

public interface AuditService {
    void logAction(Long userId, EntityType entityType, Long entityId, AuditAction action, String oldValue, String newValue);
    void logAction(Long userId, EntityType entityType, Long entityId, AuditAction action, String value);
    List<AuditLogResponse> getLogsByUserId(Long userId);
    List<AuditLogResponse> getLogsByEntityType(EntityType entityType);
    List<AuditLogResponse> getLogsByActionType(AuditAction action);
    List<AuditLogResponse> getLogsByEntityId(Long entityId);
}
