package com.sap.mobile.services.client;

import lombok.Getter;

@Getter
public class NoSuchTenantException extends ClientException {

	private final String tenantId;

	NoSuchTenantException(final String tenantId) {
		super("The tenant with id '%s' does not exist or is not subscribed to Mobile Services".formatted(tenantId));
		this.tenantId = tenantId;
	}
}
