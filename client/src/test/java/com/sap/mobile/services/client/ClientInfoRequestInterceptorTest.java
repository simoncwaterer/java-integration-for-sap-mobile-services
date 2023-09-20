package com.sap.mobile.services.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

class ClientInfoRequestInterceptorTest {

	private ClientInfoRequestInterceptor testee;

	@BeforeEach
	void setUp() throws Exception {
		testee = new ClientInfoRequestInterceptor(BuildProperties.getInstance());
	}

	@Test
	void testIntercept() throws Exception {
		HttpRequest request = Mockito.mock(HttpRequest.class);
		ClientHttpRequestExecution execution = Mockito.mock(ClientHttpRequestExecution.class);
		ClientHttpResponse response = Mockito.mock(ClientHttpResponse.class);

		HttpHeaders headers = new HttpHeaders();

		Mockito.when(request.getHeaders()).thenReturn(headers);
		Mockito.when(execution.execute(request, null)).thenReturn(response);

		assertSame(response, testee.intercept(request, null, execution));
		assertNotNull(headers.getFirst(Constants.Headers.CLIENT_VERSION_HEADER_NAME));
		assertEquals("Java", headers.getFirst(Constants.Headers.CLIENT_LANGUAGE_HEADER_NAME));

		Mockito.verify(execution).execute(request, null);
	}
}
