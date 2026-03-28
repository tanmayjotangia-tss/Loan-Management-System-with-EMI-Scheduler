package com.loanmanagementsystem.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private BigDecimal monthlyIncome;
    private BigDecimal currentEmiAmount;
    private Integer creditScore;
    private String bankAccountNumber;
    private String ifscCode;
}