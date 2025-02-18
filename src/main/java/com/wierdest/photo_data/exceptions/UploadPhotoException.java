package com.wierdest.photo_data.exceptions;

public class UploadPhotoException extends RuntimeException {
    public UploadPhotoException(String message) {
        super(message);
    }

    public UploadPhotoException(String message, Throwable cause) {
        super(message, cause);
    }
}
