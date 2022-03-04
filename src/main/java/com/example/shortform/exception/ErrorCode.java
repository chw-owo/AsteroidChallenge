package com.example.shortform.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    BAD_REQUEST(400, "BAD_REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FORBIDDEN(403, "FORBIDDEN"),
    NOT_FOUND(404, "NOT_FOUND"),
    DUPLICATE(409, "DUPLICATE"),
    INTERNAL_SERVER_ERROR(500, "COMMON_ERROR");

    private final int status;
    private final String errorCode;
}
