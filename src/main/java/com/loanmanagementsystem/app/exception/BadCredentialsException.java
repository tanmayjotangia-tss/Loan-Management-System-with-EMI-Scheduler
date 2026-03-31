package com.loanmanagementsystem.app.exception;

import org.springframework.http.HttpStatus;

public class BadCredentialsException extends ApplicationException {

  public BadCredentialsException() {
    super("Invalid username or password",
            "BAD_CREDENTIALS",
            HttpStatus.UNAUTHORIZED);
  }
}
