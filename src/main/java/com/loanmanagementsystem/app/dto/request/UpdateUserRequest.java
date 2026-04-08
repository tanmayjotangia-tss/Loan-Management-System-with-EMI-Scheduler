package com.loanmanagementsystem.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
    private String phoneNumber;

    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be positive")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal monthlyIncome;

    @DecimalMin(value = "0.0", message = "EMI cannot be negative")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal currentEmiAmount;

    @Size(max = 30, message = "Bank account number cannot exceed 30 characters")
    @Pattern(regexp = "^[0-9]+$", message = "Bank account must contain only digits")
    private String bankAccountNumber;

    @Size(min = 11, max = 11, message = "IFSC code must be 11 characters")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code")
    private String ifscCode;
}