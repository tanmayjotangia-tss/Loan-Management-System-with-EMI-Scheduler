package com.loanmanagementsystem.app.config;

import com.loanmanagementsystem.app.security.JwtAuthenticationEntryPoint;
import com.loanmanagementsystem.app.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity           // enables @PreAuthorize on service/controller methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable().cors(withDefaults()));

        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests(auth -> auth

                // Public
                .requestMatchers(
                        "/api/v1/auth/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).permitAll()

                // Borrower only
                .requestMatchers("/api/v1/loans/apply").hasRole("BORROWER")
                .requestMatchers("/api/v1/documents/**").hasRole("BORROWER")

                // Borrower + Officer
                .requestMatchers("/api/v1/loans/*/schedule")
                        .hasAnyRole("BORROWER", "LOAN_OFFICER")

                // Officer only
                .requestMatchers("/api/v1/officer/**").hasRole("LOAN_OFFICER")

                // Everything else: just be authenticated
                .anyRequest().authenticated()
        );

        http.exceptionHandling(ex ->
                ex.authenticationEntryPoint(jwtAuthenticationEntryPoint));

        http.addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}