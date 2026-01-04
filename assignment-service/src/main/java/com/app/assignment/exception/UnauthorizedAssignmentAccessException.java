package com.app.assignment.exception;

public class UnauthorizedAssignmentAccessException extends RuntimeException {
    public UnauthorizedAssignmentAccessException(String message) {
        super(message);
    }
}
