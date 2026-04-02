package com.loanmanagementsystem.app.controller;

import com.loanmanagementsystem.app.dto.request.LoginRequest;
import com.loanmanagementsystem.app.dto.request.RegisterOfficerRequest;
import com.loanmanagementsystem.app.dto.request.RegisterUserRequest;
import com.loanmanagementsystem.app.dto.request.UpdateCredentialsRequest;
import com.loanmanagementsystem.app.dto.response.ApiResponse;
import com.loanmanagementsystem.app.dto.response.AuthResponse;
import com.loanmanagementsystem.app.dto.response.UserResponse;
import com.loanmanagementsystem.app.service.AuthService;
import com.loanmanagementsystem.app.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponse response = authService.getCurrentUser(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/me/credentials")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> updateCredentials(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateCredentialsRequest request) {
        authService.updateCredentials(userDetails.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success("Credentials updated successfully."));
    }

    @PostMapping("/me/deactivate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> deactivateAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.deactivateAccount(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("Account deactivated successfully."));
    }
}
