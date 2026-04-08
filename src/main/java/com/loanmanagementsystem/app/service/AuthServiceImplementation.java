package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.request.LoginRequest;
import com.loanmanagementsystem.app.dto.request.RegisterOfficerRequest;
import com.loanmanagementsystem.app.dto.request.RegisterUserRequest;
import com.loanmanagementsystem.app.dto.request.UpdateCredentialsRequest;
import com.loanmanagementsystem.app.dto.response.AuthResponse;
import com.loanmanagementsystem.app.dto.response.UserResponse;
import com.loanmanagementsystem.app.entity.Borrower;
import com.loanmanagementsystem.app.entity.LoanOfficer;
import com.loanmanagementsystem.app.entity.User;
import com.loanmanagementsystem.app.entity.enums.AuditAction;
import com.loanmanagementsystem.app.entity.enums.EntityType;
import com.loanmanagementsystem.app.entity.enums.Role;
import com.loanmanagementsystem.app.mapper.BorrowerMapper;
import com.loanmanagementsystem.app.repository.UserRepository;
import com.loanmanagementsystem.app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final BorrowerMapper borrowerMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public AuthResponse registerBorrower(RegisterUserRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (!user.getIsActive() && request.getPhoneNumber().equals(user.getPhoneNumber())) {
                // Reactivate account
                user.setIsActive(true);
                user.setName(request.getName());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepository.save(user);
                
                return AuthResponse.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build();
            } else {
                throw new RuntimeException("Email already registered: " + request.getEmail());
            }
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered: " + request.getPhoneNumber());
        }

        Borrower borrower = borrowerMapper.toEntity(request);
        borrower.setPassword(passwordEncoder.encode(request.getPassword()));
        borrower.setRole(Role.BORROWER);
        borrower.setIsActive(true);
        borrower.setIsVerified(false);

        userRepository.save(borrower);

        auditService.logAction(borrower.getId(), EntityType.USER,borrower.getId(), AuditAction.CREATED,borrower.getName());

        return AuthResponse.builder()
                .userId(borrower.getId())
                .name(borrower.getName())
                .email(borrower.getEmail())
                .role(borrower.getRole())
                .build();
    }

    @Override
    @Transactional
    public AuthResponse registerLoanOfficer(RegisterOfficerRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (!user.getIsActive() && request.getPhoneNumber().equals(user.getPhoneNumber())) {
                // Reactivate account
                user.setIsActive(true);
                user.setName(request.getName());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepository.save(user);
                
                return AuthResponse.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build();
            } else {
                throw new RuntimeException("Email already registered: " + request.getEmail());
            }
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered: " + request.getPhoneNumber());
        }

        LoanOfficer officer = new LoanOfficer();
        officer.setName(request.getName());
        officer.setEmail(request.getEmail());
        officer.setPassword(passwordEncoder.encode(request.getPassword()));
        officer.setPhoneNumber(request.getPhoneNumber());
        officer.setRole(Role.LOAN_OFFICER);
        officer.setIsActive(true);
        officer.setIsVerified(false);
        officer.setOfficerType(request.getOfficerType());
        officer.setBranchName(request.getBranchName());
        officer.setIsAvailable(true);

        userRepository.save(officer);

        auditService.logAction(officer.getId(), EntityType.USER,officer.getId(), AuditAction.CREATED,officer.getName());

        return AuthResponse.builder()
                .userId(officer.getId())
                .name(officer.getName())
                .email(officer.getEmail())
                .role(officer.getRole())
                .build();
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean isPhoneNumberExists(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            String token = jwtTokenProvider.generateToken(authentication);

            return userRepository.findByEmailAndIsActiveTrue(request.getEmail())
                    .map(user -> AuthResponse.builder()
                            .userId(user.getId())
                            .name(user.getName())
                            .email(user.getEmail())
                            .role(user.getRole())
                            .accessToken(token)
                            .build())
                    .orElseThrow(() -> new RuntimeException("User not found after authentication"));
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    @Override
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deactivateAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsActive(false);
        userRepository.save(user);
        auditService.logAction(user.getId(), EntityType.USER,user.getId(), AuditAction.STATUS_CHANGED,"Account Active","Account Deactivate");

    }

    @Override
    @Transactional
    public void updateCredentials(Long userId, UpdateCredentialsRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String currentEmail = user.getEmail();

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!currentEmail.equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already taken");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            if (!request.getPhoneNumber().equals(user.getPhoneNumber()) && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new RuntimeException("Phone number already taken");
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getOldPassword() != null && !request.getOldPassword().isEmpty() &&
            request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid old password");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
        auditService.logAction(user.getId(), EntityType.USER,user.getId(), AuditAction.UPDATED,"Old Credentials", "New Credentials");

    }
}