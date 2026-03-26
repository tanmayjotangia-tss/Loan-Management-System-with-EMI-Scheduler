package com.loanmanagementsystem.app.mapper;

import com.loanmanagementsystem.app.dto.response.AuditLogResponse;
import com.loanmanagementsystem.app.entity.AuditLog;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "performedByUserId", source = "performedBy.id")
    @Mapping(target = "performedByUserName", source = "performedBy.name")
    AuditLogResponse toResponse(AuditLog auditLog);

    List<AuditLogResponse> toResponseList(List<AuditLog> auditLogs);
}
