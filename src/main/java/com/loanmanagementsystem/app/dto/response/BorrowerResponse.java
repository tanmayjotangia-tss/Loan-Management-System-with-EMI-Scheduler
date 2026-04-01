package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.Role;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowerResponse {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role; //----
    private Boolean isActive;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private BigDecimal monthlyIncome;
    private BigDecimal currentEmiAmount;
    private Integer creditScore;
    private String bankAccountNumber;
    private String ifscCode;
}