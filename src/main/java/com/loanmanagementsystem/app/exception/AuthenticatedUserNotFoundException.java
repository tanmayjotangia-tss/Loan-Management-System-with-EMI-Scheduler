package com.loanmanagementsystem.app.exception;

import org.springframework.http.HttpStatus;

public class AuthenticatedUserNotFoundException extends ApplicationException {

    public AuthenticatedUserNotFoundException() {
        super("Authenticated user not found",
                "AUTH_USER_NOT_FOUND",
                HttpStatus.UNAUTHORIZED);
    }
}
