package com.loanmanagementsystem.app.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApplicationException {

    public BadRequestException(String message) {
        super(message,
                "BAD_REQUEST",
                HttpStatus.BAD_REQUEST);
    }
}
