package com.example.cmtProject.comm.exception;

public class DocFormNotFoundException extends RuntimeException {
    public DocFormNotFoundException(String message) {
        super(message);
    }
    
    public DocFormNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}