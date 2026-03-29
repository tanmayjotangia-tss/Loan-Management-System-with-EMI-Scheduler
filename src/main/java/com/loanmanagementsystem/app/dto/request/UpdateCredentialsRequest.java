package com.loanmanagementsystem.app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCredentialsRequest {

    private String email;

    private String phoneNumber;

    private String oldPassword;

    private String newPassword;
}
