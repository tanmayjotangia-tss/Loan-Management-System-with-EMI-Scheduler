package com.loanmanagementsystem.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterBorrowerRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    private String phoneNumber;

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be positive")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal monthlyIncome;

    @DecimalMin(value = "0.0", message = "Current EMI amount cannot be negative")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal currentEmiAmount;

    @NotBlank(message = "Bank account number is required")
    @Size(max = 30, message = "Bank account number cannot exceed 30 characters")
    @Pattern(regexp = "^[0-9]+$", message = "Bank account number must contain only digits")
    private String bankAccountNumber;

    @NotBlank(message = "IFSC code is required")
    @Size(min = 11, max = 11, message = "IFSC code must be exactly 11 characters")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
    private String ifscCode;
}