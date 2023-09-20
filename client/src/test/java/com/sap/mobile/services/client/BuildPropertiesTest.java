package com.sap.mobile.services.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;


import org.junit.jupiter.api.Test;

import com.sap.mobile.services.client.BuildProperties;

class BuildPropertiesTest {

	@Test
	void testBuildVersion() throws Exception {
		BuildProperties props = BuildProperties.getInstance();
		assertNotNull(props.getVersion());
	}
}
