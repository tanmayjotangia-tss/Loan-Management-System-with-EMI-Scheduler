package com.loanmanagementsystem.app.repository;

import com.loanmanagementsystem.app.entity.AuditLog;
import com.loanmanagementsystem.app.entity.enums.AuditAction;
import com.loanmanagementsystem.app.entity.enums.EntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByPerformedByUserIdOrderByTimestampDesc(Long userId);

    List<AuditLog> findAllByEntityType(EntityType entityType);

    List<AuditLog> findAllByAction(AuditAction action);

    List<AuditLog> findAllByEntityId(Long entityId);

    List<AuditLog> findAllByEntityTypeAndEntityId(EntityType entityType, Long entityId);
}