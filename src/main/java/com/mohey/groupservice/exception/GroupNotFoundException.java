package com.mohey.groupservice.exception;

public class GroupNotFoundException extends RuntimeException {
	public GroupNotFoundException(String message) {
		super(message);
	}
}