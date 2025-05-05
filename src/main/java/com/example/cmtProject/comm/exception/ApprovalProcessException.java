package com.example.cmtProject.comm.exception;

public class ApprovalProcessException extends RuntimeException {
    public ApprovalProcessException(String message) {
        super(message);
    }
    
    public ApprovalProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}