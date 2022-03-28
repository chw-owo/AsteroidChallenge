package com.example.shortform.exception;

import lombok.Getter;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Getter
public class FileUploadException extends MaxUploadSizeExceededException {

    private ErrorCode errorCode;

    public FileUploadException(Long size) {
        super(size);
        this.errorCode = ErrorCode.DUPLICATE;
    }
}