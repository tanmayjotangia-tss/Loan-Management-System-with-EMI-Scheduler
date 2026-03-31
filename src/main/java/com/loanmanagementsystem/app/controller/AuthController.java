package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.request.LoginRequest;
import com.loanmanagementsystem.app.dto.request.RegisterOfficerRequest;
import com.loanmanagementsystem.app.dto.request.RegisterUserRequest;
import com.loanmanagementsystem.app.dto.request.UpdateCredentialsRequest;
import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.AuthResponse;
import com.loanmanagementsystem.app.dto.response.UserResponse;
import com.loanmanagementsystem.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/borrower")
    public ResponseEntity<ApiResponse<AuthResponse>> registerBorrower(@Valid @RequestBody RegisterUserRequest request) {
        AuthResponse response = authService.registerBorrower(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PostMapping("/register/officer")
    public ResponseEntity<ApiResponse<AuthResponse>> registerLoanOfficer(@Valid @RequestBody RegisterOfficerRequest request) {
        AuthResponse response = authService.registerLoanOfficer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse response = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/me/credentials")
    public ResponseEntity<ApiResponse<String>> updateCredentials(@Valid @RequestBody UpdateCredentialsRequest request) {
        authService.updateCredentials(request);
        return ResponseEntity.ok(ApiResponse.success("Credentials updated successfully."));
    }

    @PostMapping("/me/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateAccount() {
        authService.deactivateAccount();
        return ResponseEntity.ok(ApiResponse.success("Account deactivated successfully."));
    }

    @GetMapping("/exists/email")
    public ResponseEntity<ApiResponse<Boolean>> isEmailExists(@RequestParam String email) {
        boolean exists = authService.isEmailExists(email);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/exists/phone")
    public ResponseEntity<ApiResponse<Boolean>> isPhoneNumberExists(@RequestParam String phoneNumber) {
        boolean exists = authService.isPhoneNumberExists(phoneNumber);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
