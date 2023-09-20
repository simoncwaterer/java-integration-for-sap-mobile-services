package com.sap.mobile.services.client.push;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DTOTest {
	@Test
	void testDTOGcmNotificationTllFormat() throws Exception {
		assertNull(DTOGcmNotification.ttlFormat(null));
		assertEquals("3s", DTOGcmNotification.ttlFormat(Duration.ofSeconds(3)));
		assertEquals("3.000000001s",
				DTOGcmNotification.ttlFormat(Duration.ofSeconds(3).plus(Duration.ofNanos(1))));
		assertEquals("3.010000001s",
				DTOGcmNotification
						.ttlFormat(Duration.ofSeconds(3).plus(Duration.ofMillis(10)).plus(Duration.ofNanos(1))));
	}

	@Test
	void testDTONotificationStatusResponse() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		DTOGetNotificationStatusResponse response = mapper.readValue(
				DTOTest.class.getResourceAsStream("/payloads/response-get-notification-status.json"),
				DTOGetNotificationStatusResponse.class);
		assertNotNull(response.getStatusDetails().getCaller());
	}
}
