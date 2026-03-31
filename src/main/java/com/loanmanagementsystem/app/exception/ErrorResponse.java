package com.loanmanagementsystem.app.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String path;
    private Instant timestamp;
    private String error;
}
