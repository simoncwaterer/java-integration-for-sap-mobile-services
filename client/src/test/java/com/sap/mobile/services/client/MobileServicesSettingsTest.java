package com.sap.mobile.services.client;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;

class MobileServicesSettingsTest {

	@Test
	void testSettings() throws Exception {
		MobileServicesSettings settings = MobileServicesSettings.fromResource("mobileservices-config.json");
		assertEquals(
				"https://service.key.url.com/2787d5ea-17cb-4c71-85ca-a9600d4b97c9",
				settings.getServices().stream().filter(s -> "push".equals(s.getName())).findFirst().get()
						.getServiceKeys().get(0)
						.getUrl());
		assertEquals("apiKey", settings.getServices()
				.stream().filter(s -> "push".equals(s.getName())).findFirst().get().getServiceKeys().get(0)
				.getApiKey());
	}
}
