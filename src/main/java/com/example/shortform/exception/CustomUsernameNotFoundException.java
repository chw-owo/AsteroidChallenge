package com.example.shortform.exception;

import lombok.Getter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Getter
public class CustomUsernameNotFoundException extends UsernameNotFoundException {

    private ErrorCode errorCode;

    public CustomUsernameNotFoundException(String msg) {
        super(msg);
        this.errorCode = ErrorCode.NOT_FOUND;
    }
}
