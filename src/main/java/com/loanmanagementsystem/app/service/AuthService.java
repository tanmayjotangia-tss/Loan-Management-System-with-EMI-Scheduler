package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.request.*;
import com.loanmanagementsystem.app.dto.response.AuthResponse;
import com.loanmanagementsystem.app.dto.response.UserResponse;

public interface AuthService {

    AuthResponse registerBorrower(RegisterBorrowerRequest request);

    AuthResponse registerLoanOfficer(RegisterOfficerRequest request);

    AuthResponse registerAdmin(RegisterAdminRequest request);

    boolean isEmailExists(String email);

    boolean isPhoneNumberExists(String phoneNumber);

    AuthResponse login(LoginRequest request);

    UserResponse getCurrentUser(Long userId);

    void deactivateAccount(Long userId);

    void updateCredentials(Long userId, UpdateCredentialsRequest request);
}
