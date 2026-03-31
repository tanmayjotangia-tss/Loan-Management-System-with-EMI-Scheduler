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
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user;
        if (identifier.contains("@")) {
            user = userRepository.findByEmailAndIsActiveTrue(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "No active account found for email: " + identifier));
        } else {
            try {
                Long id = Long.parseLong(identifier);
                user = userRepository.findById(id)
                        .filter(User::getIsActive)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "No active account found for id: " + id));
            } catch (NumberFormatException e) {
                throw new UsernameNotFoundException("Invalid identifier: " + identifier);
            }
        }

        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), user.getRole());
    }
}
