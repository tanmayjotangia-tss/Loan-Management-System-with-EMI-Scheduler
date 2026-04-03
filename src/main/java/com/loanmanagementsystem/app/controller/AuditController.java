package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.AuditLogResponse;
import com.loanmanagementsystem.app.entity.enums.AuditAction;
import com.loanmanagementsystem.app.entity.enums.EntityType;
import com.loanmanagementsystem.app.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Validated
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getLogsByUserId(
            @PathVariable Long userId) {

        List<AuditLogResponse> responses = auditService.getLogsByUserId(userId);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Audit logs fetched successfully for user", responses)
        );
    }

    @GetMapping("/entity-type/{entityType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getLogsByEntityType(
            @PathVariable EntityType entityType) {

        List<AuditLogResponse> responses = auditService.getLogsByEntityType(entityType);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Audit logs fetched successfully by entity type", responses)
        );
    }

    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getLogsByActionType(
            @PathVariable AuditAction action) {

        List<AuditLogResponse> responses = auditService.getLogsByActionType(action);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Audit logs fetched successfully by action type", responses)
        );
    }

    @GetMapping("/entity/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getLogsByEntityId(
            @PathVariable Long entityId) {

        List<AuditLogResponse> responses = auditService.getLogsByEntityId(entityId);

        return ResponseEntity.ok(
                ApiResponse.success(200, "Audit logs fetched successfully for entity", responses)
        );
    }
}