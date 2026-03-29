package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.request.LoginRequest;
import com.loanmanagementsystem.app.dto.request.RegisterOfficerRequest;
import com.loanmanagementsystem.app.dto.request.RegisterUserRequest;
import com.loanmanagementsystem.app.dto.request.UpdateCredentialsRequest;
import com.loanmanagementsystem.app.dto.response.AuthResponse;
import com.loanmanagementsystem.app.dto.response.UserResponse;

public interface AuthService {

    AuthResponse registerBorrower(RegisterUserRequest request);

    AuthResponse registerLoanOfficer(RegisterOfficerRequest request);

    boolean isEmailExists(String email);

    boolean isPhoneNumberExists(String phoneNumber);

    AuthResponse login(LoginRequest request);

    UserResponse getCurrentUser();

    void deactivateAccount();

    void updateCredentials(UpdateCredentialsRequest request);
}
