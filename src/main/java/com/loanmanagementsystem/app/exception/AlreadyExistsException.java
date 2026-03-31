package com.loanmanagementsystem.app.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ApplicationException {

    public AlreadyExistsException(String resource, Object identifier) {
        super(resource + " already exists: " + identifier,
                "ALREADY_EXISTS",
                HttpStatus.CONFLICT);
    }
}
