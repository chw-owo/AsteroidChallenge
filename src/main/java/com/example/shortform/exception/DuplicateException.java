package com.example.shortform.exception;

import lombok.Getter;

@Getter
public class DuplicateException extends RuntimeException {

    private ErrorCode errorCode;

    public DuplicateException(String message) {
        super(message);
        this.errorCode = ErrorCode.DUPLICATE;
    }
}
