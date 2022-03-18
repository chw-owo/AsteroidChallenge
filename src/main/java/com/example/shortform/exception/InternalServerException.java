package com.example.shortform.exception;

import lombok.Getter;

@Getter
public class InternalServerException extends Throwable {

    private ErrorCode errorCode;

    public InternalServerException(String message) {
        super(message);
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    }
}
