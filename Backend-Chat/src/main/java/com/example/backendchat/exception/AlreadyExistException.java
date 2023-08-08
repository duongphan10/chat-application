package com.example.backendchat.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class AlreadyExistException extends RuntimeException {

    private String message;

    private HttpStatus status;

    private String[] params;

    public AlreadyExistException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.message = message;
    }

    public AlreadyExistException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public AlreadyExistException(String message, String[] params) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.message = message;
        this.params = params;
    }

    public AlreadyExistException(HttpStatus status, String message, String[] params) {
        super(message);
        this.status = status;
        this.message = message;
        this.params = params;
    }

}