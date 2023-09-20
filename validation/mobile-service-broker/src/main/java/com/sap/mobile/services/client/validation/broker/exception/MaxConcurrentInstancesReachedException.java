package com.sap.mobile.services.client.validation.broker.exception;

public class MaxConcurrentInstancesReachedException extends Exception {

	public MaxConcurrentInstancesReachedException(final int maxInstances) {
		super("The maximum number of concurrent apps (%d) has been reached".formatted(maxInstances));
	}

}
