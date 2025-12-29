package com.app.booking.exception;

public class BusinessValidationException extends RuntimeException {
	public BusinessValidationException(String message) {
		super(message);
	}
}
