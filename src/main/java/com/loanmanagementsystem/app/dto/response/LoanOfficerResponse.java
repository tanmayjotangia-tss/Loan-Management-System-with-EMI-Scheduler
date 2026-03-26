package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.OfficerType;
import com.loanmanagementsystem.app.entity.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanOfficerResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
    private Boolean isActive;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String employeeNumber;
    private OfficerType officerType;
    private String branchName;
    private Boolean isAvailable;
}
