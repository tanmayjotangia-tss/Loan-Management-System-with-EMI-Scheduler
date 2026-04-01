package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.AuditLogResponse;
import com.loanmanagementsystem.app.entity.enums.AuditAction;
import com.loanmanagementsystem.app.entity.enums.EntityType;
import com.loanmanagementsystem.app.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public record AuditLogRequest(
            Long userId,
            EntityType entityType,
            Long entityId,
            AuditAction action,
            String oldValue,
            String newValue
    ) {}

    @PostMapping("/log")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> logAction(@RequestBody AuditLogRequest request) {
        auditService.logAction(
                request.userId(),
                request.entityType(),
                request.entityId(),
                request.action(),
                request.oldValue(),
                request.newValue()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Audit log created successfully", null));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getLogsByUserId(@PathVariable Long userId) {
        List<AuditLogResponse> responses = auditService.getLogsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/entity-type/{entityType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getLogsByEntityType(@PathVariable EntityType entityType) {
        List<AuditLogResponse> responses = auditService.getLogsByEntityType(entityType);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getLogsByActionType(@PathVariable AuditAction action) {
        List<AuditLogResponse> responses = auditService.getLogsByActionType(action);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/entity/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getLogsByEntityId(@PathVariable Long entityId) {
        List<AuditLogResponse> responses = auditService.getLogsByEntityId(entityId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
