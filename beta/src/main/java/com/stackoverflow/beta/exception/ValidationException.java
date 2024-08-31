package com.stackoverflow.beta.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ValidationException extends RuntimeException{
    private Object response;
    private HttpStatus status;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.response = message;
    }
}
