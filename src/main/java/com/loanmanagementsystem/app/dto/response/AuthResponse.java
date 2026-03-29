package com.loanmanagementsystem.app.dto.response;

import com.loanmanagementsystem.app.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private Long   userId;
    private String name;
    private String email;
    private Role   role;

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";
}
