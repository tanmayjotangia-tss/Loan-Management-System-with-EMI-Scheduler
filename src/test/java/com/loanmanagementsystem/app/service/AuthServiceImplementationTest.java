package com.loanmanagementsystem.app.service;

import com.loanmanagementsystem.app.dto.request.LoginRequest;
import com.loanmanagementsystem.app.dto.request.RegisterUserRequest;
import com.loanmanagementsystem.app.dto.request.UpdateCredentialsRequest;
import com.loanmanagementsystem.app.dto.response.AuthResponse;
import com.loanmanagementsystem.app.entity.Borrower;
import com.loanmanagementsystem.app.entity.User;
import com.loanmanagementsystem.app.entity.enums.Role;
import com.loanmanagementsystem.app.mapper.BorrowerMapper;
import com.loanmanagementsystem.app.repository.UserRepository;
import com.loanmanagementsystem.app.security.JwtTokenProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplementationTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private BorrowerMapper borrowerMapper;

    @InjectMocks
    private AuthServiceImplementation authService;

    @Test
    void registerBorrower_success() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setName("John");
        request.setEmail("john@test.com");
        request.setPassword("1234");
        request.setPhoneNumber("9999999999");

        Borrower borrower = new Borrower();
        borrower.setName("John");
        borrower.setEmail("john@test.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(false);
        when(borrowerMapper.toEntity(request)).thenReturn(borrower);
        when(passwordEncoder.encode("1234")).thenReturn("encoded");

        AuthResponse response = authService.registerBorrower(request);

        assertNotNull(response);
        assertEquals("John", response.getName());
        assertEquals("john@test.com", response.getEmail());
        assertEquals(Role.BORROWER, response.getRole());

        verify(userRepository).save(any(Borrower.class));
    }

    @Test
    void registerBorrower_emailAlreadyExists_shouldThrow() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("test@test.com");
        request.setPhoneNumber("999");

        User existing = new User();
        existing.setIsActive(true);

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class,
                () -> authService.registerBorrower(request));
    }

    @Test
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("1234");

        Authentication authentication = mock(Authentication.class);

        User user = new User();
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setRole(Role.BORROWER);
        user.setIsActive(true);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtTokenProvider.generateToken(authentication))
                .thenReturn("token123");

        when(userRepository.findByEmailAndIsActiveTrue(request.getEmail()))
                .thenReturn(Optional.of(user));

        AuthResponse response = authService.login(request);

        assertEquals("token123", response.getAccessToken());
        assertEquals("Test", response.getName());
    }

    @Test
    void login_invalidCredentials_shouldThrow() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad"));

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }


    @Test
    void isEmailExists() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertTrue(authService.isEmailExists("test@test.com"));
    }

    @Test
    void getCurrentUser_success() {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("test@test.com");
        user.setPhoneNumber("9999999999");
        user.setRole(Role.BORROWER);
        user.setIsActive(true);
        user.setIsVerified(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
 
        var response = authService.getCurrentUser(1L);

        assertNotNull(response);
        assertEquals("John", response.getName());
        assertEquals("test@test.com", response.getEmail());
        assertEquals(Role.BORROWER, response.getRole());
    }

    @Test
    void getCurrentUser_userNotFound_shouldThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
 
        assertThrows(RuntimeException.class, () -> authService.getCurrentUser(1L));
    }

    @Test
    void updateCredentials_success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPhoneNumber("999");
        user.setPassword("encodedOld");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("oldPass", "encodedOld"))
                .thenReturn(true);

        when(passwordEncoder.encode("newPass"))
                .thenReturn("encodedNew");

        var request = new UpdateCredentialsRequest();
        request.setEmail("new@test.com");
        request.setPhoneNumber("888");
        request.setOldPassword("oldPass");
        request.setNewPassword("newPass");

        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("888")).thenReturn(false);
 
        authService.updateCredentials(1L, request);

        assertEquals("new@test.com", user.getEmail());
        assertEquals("888", user.getPhoneNumber());
        assertEquals("encodedNew", user.getPassword());

        verify(userRepository).save(user);
    }
}
