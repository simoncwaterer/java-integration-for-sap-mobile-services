package com.sap.mobile.services.client.validation.broker.exception;

public class NoSuchServiceInstanceException extends Exception {
	public NoSuchServiceInstanceException(final String name) {
		super("Service instance with name '%s' not found".formatted(name));
	}
}
