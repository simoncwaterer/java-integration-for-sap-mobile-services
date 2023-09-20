package com.sap.mobile.services.client.push;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.sap.cloud.security.xsuaa.client.OAuth2TokenResponse;
import com.sap.cloud.security.xsuaa.tokenflows.ClientCredentialsTokenFlow;
import com.sap.mobile.services.client.MobileServicesBinding;
import com.sap.mobile.services.client.MobileServicesSettings;
import com.sap.mobile.services.client.XsuaaTokenFlowFactory;
import com.sap.mobile.services.client.push.mock.MockPushServer;

class PushClientMockedTest {

	private static final String DEFAULT_USER_ID = "john.doe@example.com";
	private static final String DEFAULT_DEVICE_ID = "25faa19e-1b70-429c-8e24-9c9dd363e5b7";

	private RestTemplatePushClient testee;
	private MockPushServer mockPushServer;

	@BeforeEach
	void setUp() throws Exception {
		final MobileServicesSettings settings = MobileServicesSettings.fromResource("mobileservices-config.json");
		this.testee = (RestTemplatePushClient) new PushClientBuilder()
				.build(settings);
		this.mockPushServer = new MockPushServer(testee.getRestTemplate(), settings);
	}

	@Test
	void testPushToApplication() throws Exception {
		mockPushServer.expectPushToApplication()
				.withJsonBodyFromResource("payloads/request-push-to-application-alert.json");

		testee.pushToApplication(PushPayload.builder()
				.alert("Hello World")
				.build());

		assertThat(mockPushServer, MockPushServer.hasBeenVerified());
	}

	@Test
	void testPushToApplicationDeviceSpecific() throws Exception {
		mockPushServer.expectPushToApplication()
				.withJsonBodyFromResource("payloads/request-push-to-application-device-specific.json");

		testee.pushToApplication(PushPayload.builder()
				.alert("Hello World")
				.gcm(GcmNotification.builder()
						.title("Hello World from GCM")
						.body("Sent by the CPMS Push Library")
						.build())
				.apns(ApnsNotification.builder()
						.alertTitle("Hello World from APNS")
						.alertSubtitle("With some subtitle")
						.alertBody("Sent by the CPMS Push Library")
						.build())
				.build());

		assertThat(mockPushServer, MockPushServer.hasBeenVerified());
	}

	@Test
	void testPushToUser() throws Exception {
		mockPushServer.expectPushToUser(DEFAULT_USER_ID, DEFAULT_DEVICE_ID)
				.withJsonBodyFromResource("payloads/request-push-to-user-alert.json");

		final PushResponse response = testee.pushToDevice(DEFAULT_USER_ID, DEFAULT_DEVICE_ID, PushPayload.builder()
				.alert("Hello World")
				.build());

		assertThat(response.getStatus(), is(nullValue()));

		assertThat(response.getResults(), hasSize(1));
		assertThat(response.getResults().get(0).getCode(), is(0));
		assertThat(response.getResults().get(0).getMessage(), is("success"));
		assertThat(response.getResults().get(0).getNotificationId(), is(not(emptyOrNullString())));
		assertThat(response.getResults().get(0).getTarget(), is(not(emptyOrNullString())));
		assertThat(mockPushServer, MockPushServer.hasBeenVerified());
	}

	@Test
	void testPushToUserNoSuchRegistration() throws Exception {
		assertThrows(NoMessageSentException.class, () -> {
			mockPushServer.expectPushToUser(DEFAULT_USER_ID, DEFAULT_DEVICE_ID)
					.andRespond().withNoSuchRegistrationError();

			testee.pushToDevice(DEFAULT_USER_ID, DEFAULT_DEVICE_ID, PushPayload.builder()
					.alert("Hello World")
					.build());
		});
	}

	@Test
	void testPushToUsers() throws Exception {
		mockPushServer.expectPushToUsers()
				.withJsonBodyFromResource("payloads/request-push-to-users-alert.json");

		final PushResponse response = testee.pushToUsers(Arrays.asList("john.doe@example.com", "jane.doe@example.com"),
				PushPayload.builder()
						.alert("Hello World")
						.build());

		assertThat(response.getStatus(), is(nullValue()));

		assertThat(response.getResults(), hasSize(2));
		assertThat(response.getResults().get(0).getCode(), is(0));
		assertThat(response.getResults().get(0).getMessage(), is("success"));
		assertThat(response.getResults().get(0).getNotificationId(), is(not(emptyOrNullString())));
		assertThat(response.getResults().get(0).getTarget(), is(not(emptyOrNullString())));
		assertThat(response.getResults().get(1).getCode(), is(0));
		assertThat(mockPushServer, MockPushServer.hasBeenVerified());
	}

	@Test
	void testPushToUsersNoSuchRegistration() throws Exception {
		assertThrows(NoMessageSentException.class, () -> {
			mockPushServer.expectPushToUsers()
					.withJsonBodyFromResource("payloads/request-push-to-users-alert.json")
					.andRespond().withNoSuchRegistrationError();

			testee.pushToUsers(Arrays.asList("john.doe@example.com", "jane.doe@example.com"),
					PushPayload.builder()
							.alert("Hello World")
							.build());
		});
	}

	@Test
	void testWithServiceBinding() throws Exception {
		final MobileServicesBinding binding = MobileServicesBinding.fromResource("mobileservices-binding-shared.json");
		final XsuaaTokenFlowFactory factory = Mockito.mock(XsuaaTokenFlowFactory.class);
		final ClientCredentialsTokenFlow flow = Mockito.mock(ClientCredentialsTokenFlow.class);

		Mockito.when(factory.createClientCredentialsTokenFlow(binding)).thenReturn(flow);
		this.testee = (RestTemplatePushClient) new PushClientBuilder()
				.withTokenFlowFactory(factory)
				.build(binding);
		this.mockPushServer = new MockPushServer(testee.getRestTemplate(), binding);
		mockPushServer.expectPushToApplication()
				.withJsonBodyFromResource("payloads/request-push-to-application-alert.json")
				.withBearerToken("my-secret-jwt");

		final OAuth2TokenResponse response = new OAuth2TokenResponse("my-secret-jwt", 60, "refresh-me");
		Mockito.when(flow.execute()).thenReturn(response);

		testee.pushToApplication(PushPayload.builder()
				.alert("Hello World")
				.build());

		assertThat(mockPushServer, MockPushServer.hasBeenVerified());
	}

	@Test
	void testWithServiceKeyAndTenantResolver() throws Exception {
		final String tenantZoneId = UUID.randomUUID().toString();
		final MobileServicesSettings settings = MobileServicesSettings.fromResource("mobileservices-config.json");
		this.testee = (RestTemplatePushClient) new PushClientBuilder()
				.withTenantSupplier(() -> Optional.of(tenantZoneId))
				.build(settings);
		this.mockPushServer = new MockPushServer(testee.getRestTemplate(), settings);

		mockPushServer.expectPushToApplication()
				.withJsonBodyFromResource("payloads/request-push-to-application-alert.json")
				.forTenant(tenantZoneId);

		testee.pushToApplication(PushPayload.builder()
				.alert("Hello World")
				.build());

		assertThat(mockPushServer, MockPushServer.hasBeenVerified());
	}

	@Test
	void testWithServiceBindingAndTenantResolverTenantModeShared() throws Exception {
		final MobileServicesBinding binding = MobileServicesBinding.fromResource("mobileservices-binding-shared.json");
		final XsuaaTokenFlowFactory factory = Mockito.mock(XsuaaTokenFlowFactory.class);
		final ClientCredentialsTokenFlow flow = Mockito.mock(ClientCredentialsTokenFlow.class);

		Mockito.when(factory.createClientCredentialsTokenFlow(binding)).thenReturn(flow);
		this.testee = (RestTemplatePushClient) new PushClientBuilder()
				.withTokenFlowFactory(factory)
				.withTenantSupplier(() -> Optional.of("49ed9cfd-1e52-431e-8a9a-5dbe880ab9fb"))
				.build(binding);
		this.mockPushServer = new MockPushServer(testee.getRestTemplate(), binding);
		mockPushServer.expectPushToApplication()
				.withJsonBodyFromResource("payloads/request-push-to-application-alert.json")
				.withBearerToken("my-secret-jwt");

		final OAuth2TokenResponse response = new OAuth2TokenResponse("my-secret-jwt", 60, "refresh-me");
		Mockito.when(flow.zoneId("49ed9cfd-1e52-431e-8a9a-5dbe880ab9fb")).thenReturn(flow);
		Mockito.when(flow.execute()).thenReturn(response);

		testee.pushToApplication(PushPayload.builder()
				.alert("Hello World")
				.build());

		assertThat(mockPushServer, MockPushServer.hasBeenVerified());
		Mockito.verify(flow).zoneId("49ed9cfd-1e52-431e-8a9a-5dbe880ab9fb");
	}

	@Test
	void testWithServiceBindingAndTenantResolverTenantModeDedicated() throws Exception {
		final MobileServicesBinding binding = MobileServicesBinding.fromResource("mobileservices-binding-dedicated.json");
		final XsuaaTokenFlowFactory factory = Mockito.mock(XsuaaTokenFlowFactory.class);
		final ClientCredentialsTokenFlow flow = Mockito.mock(ClientCredentialsTokenFlow.class);

		Mockito.when(factory.createClientCredentialsTokenFlow(binding)).thenReturn(flow);
		this.testee = (RestTemplatePushClient) new PushClientBuilder()
				.withTokenFlowFactory(factory)
				.withTenantSupplier(() -> Optional.of("49ed9cfd-1e52-431e-8a9a-5dbe880ab9fb"))
				.build(binding);
		this.mockPushServer = new MockPushServer(testee.getRestTemplate(), binding);
		mockPushServer.expectPushToApplication()
				.withJsonBodyFromResource("payloads/request-push-to-application-alert.json")
				.withBearerToken("my-secret-jwt");

		final OAuth2TokenResponse response = new OAuth2TokenResponse("my-secret-jwt", 60, "refresh-me");
		Mockito.when(flow.execute()).thenReturn(response);

		testee.pushToApplication(PushPayload.builder()
				.alert("Hello World")
				.build());

		assertThat(mockPushServer, MockPushServer.hasBeenVerified());
		Mockito.verify(flow, Mockito.never()).zoneId("49ed9cfd-1e52-431e-8a9a-5dbe880ab9fb");
	}
}
