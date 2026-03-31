package com.loanmanagementsystem.app.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public abstract class ApplicationException extends RuntimeException {
    private final String errorCode;
    private final HttpStatus status;

    public ApplicationException(String message, String errorCode, HttpStatus status){
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
