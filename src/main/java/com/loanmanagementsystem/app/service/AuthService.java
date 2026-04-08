package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.request.LoginRequest;
import com.loanmanagementsystem.app.dto.request.RegisterOfficerRequest;
import com.loanmanagementsystem.app.dto.request.RegisterBorrowerRequest;
import com.loanmanagementsystem.app.dto.request.UpdateCredentialsRequest;
import com.loanmanagementsystem.app.dto.response.AuthResponse;
import com.loanmanagementsystem.app.dto.response.UserResponse;

public interface AuthService {

    AuthResponse registerBorrower(RegisterBorrowerRequest request);

    AuthResponse registerLoanOfficer(RegisterOfficerRequest request);

    boolean isEmailExists(String email);

    boolean isPhoneNumberExists(String phoneNumber);

    AuthResponse login(LoginRequest request);

    UserResponse getCurrentUser(Long userId);

    void deactivateAccount(Long userId);

    void updateCredentials(Long userId, UpdateCredentialsRequest request);
}
