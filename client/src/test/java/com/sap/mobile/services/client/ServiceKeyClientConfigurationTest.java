package com.sap.mobile.services.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceKeyClientConfigurationTest {

	private ServiceKeyClientConfiguration testee;

	@BeforeEach
	void prepare() {
		testee = ServiceKeyClientConfiguration.builder()
				.buildProperties(BuildProperties.getInstance())
				.applicationId("com.sap.mobile.sevices.application")
				.serviceKey(MobileServicesSettings.ServiceKey.builder()
						.apiKey("api-key")
						.alias("mobileservices")
						.url("https://mobile-services.tld/e0fc2a72-cac1-472a-ab8e-6478a1e46c40")
						.build())
				.build();
	}

	@Test
	void testGetRootUri() {
		assertThat(testee.getRootUri(), is("https://mobile-services.tld/e0fc2a72-cac1-472a-ab8e-6478a1e46c40"));
	}

	@Test
	void testGetAuthInterceptor() {
		assertThat(testee.getAuthInterceptor(), is(instanceOf(ApiKeyAuthorizationRequestInterceptor.class)));
	}

}
