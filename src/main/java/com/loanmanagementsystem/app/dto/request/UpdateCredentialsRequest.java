package com.loanmanagementsystem.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCredentialsRequest {

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian phone number")
    private String phoneNumber;

    @Size(min = 8, max = 100, message = "Old password must be between 8 and 100 characters")
    private String oldPassword;

    @Size(min = 8, max = 100, message = "New password must be between 8 and 100 characters")
    private String newPassword;
}