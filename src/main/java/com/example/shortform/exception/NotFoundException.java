package com.example.shortform.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{

    private ErrorCode errorCode;

    public NotFoundException(String message) {
        super(message);
        this.errorCode = ErrorCode.NOT_FOUND;
    }
}
