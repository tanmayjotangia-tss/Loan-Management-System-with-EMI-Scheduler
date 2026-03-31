package com.loanmanagementsystem.app.exception;

import org.springframework.http.HttpStatus;

public class NotAuthorizedException extends ApplicationException {

    public NotAuthorizedException() {
        super("You are not authorized to perform this action",
                "NOT_AUTHORIZED",
                HttpStatus.FORBIDDEN);
    }
}
