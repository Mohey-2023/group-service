package com.mohey.groupservice.exception;

public class NotGroupLeaderException extends RuntimeException {
	public NotGroupLeaderException(String message) {
		super(message);
	}
}