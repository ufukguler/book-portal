package com.bookportal.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class CustomFileUploadException extends RuntimeException {
    public CustomFileUploadException() {
        super("An error occured while uploading file to aws s3.");
    }
}
