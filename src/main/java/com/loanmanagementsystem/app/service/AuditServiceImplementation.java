package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.response.AuditLogResponse;
import com.loanmanagementsystem.app.entity.AuditLog;
import com.loanmanagementsystem.app.entity.User;
import com.loanmanagementsystem.app.entity.enums.AuditAction;
import com.loanmanagementsystem.app.entity.enums.EntityType;
import com.loanmanagementsystem.app.mapper.AuditLogMapper;
import com.loanmanagementsystem.app.repository.AuditLogRepository;
import com.loanmanagementsystem.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImplementation implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public void logAction(Long userId, EntityType entityType, Long entityId, AuditAction action, String oldValue, String newValue) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        AuditLog auditLog = AuditLog.builder()
                .performedBy(user)
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();

        auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLogResponse> getLogsByUserId(Long userId) {
        return auditLogRepository.findAllByPerformedByUserIdOrderByTimestampDesc(userId)
                .stream()
                .map(auditLogMapper :: toResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getLogsByEntityType(EntityType entityType) {
        return auditLogRepository.findAllByEntityType(entityType)
                .stream()
                .map(auditLogMapper :: toResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getLogsByActionType(AuditAction action) {
        return auditLogRepository.findAllByAction(action)
                .stream()
                .map(auditLogMapper :: toResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getLogsByEntityId(Long entityId) {
        return auditLogRepository.findAllByEntityId(entityId)
                .stream()
                .map(auditLogMapper :: toResponse)
                .toList();
    }
}
