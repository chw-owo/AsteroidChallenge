package com.example.shortform.exception;

import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {

    private ErrorCode errorCode;

    public ForbiddenException(String message) {
        super(message);
        this.errorCode = ErrorCode.FORBIDDEN;
    }
}
