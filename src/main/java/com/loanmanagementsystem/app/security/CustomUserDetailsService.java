package com.loanmanagementsystem.app.security;

import com.loanmanagementsystem.app.entity.User;
import com.loanmanagementsystem.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No active account found for email: " + email));

        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), user.getRole());
    }
}
