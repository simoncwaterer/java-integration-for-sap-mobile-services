/**
 * (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * No part of this publication may be reproduced or transmitted in any form or for any purpose
 * without the express permission of SAP SE. The information contained herein may be changed
 * without prior notice.
 */

package com.sap.mobile.services.client.push;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;

public class DTOTest {
	@Test
	public void testDTOGcmNotificationTllFormat() throws Exception {
		Assert.assertNull(DTOGcmNotification.ttlFormat(null));
		Assert.assertEquals("3s", DTOGcmNotification.ttlFormat(Duration.ofSeconds(3)));
		Assert.assertEquals("3.000000001s",
				DTOGcmNotification.ttlFormat(Duration.ofSeconds(3).plus(Duration.ofNanos(1))));
		Assert.assertEquals("3.010000001s",
				DTOGcmNotification
						.ttlFormat(Duration.ofSeconds(3).plus(Duration.ofMillis(10)).plus(Duration.ofNanos(1))));
	}

	@Test
	public void testDTONotificationStatusResponse() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		DTOGetNotificationStatusResponse response = mapper.readValue(
				DTOTest.class.getResourceAsStream("/payloads/response-get-notification-status.json"),
				DTOGetNotificationStatusResponse.class);
		Assert.assertNotNull(response.getStatusDetails().getCaller());
	}
}
