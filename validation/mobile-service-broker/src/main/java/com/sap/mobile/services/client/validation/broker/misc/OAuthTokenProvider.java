package com.sap.mobile.services.client.validation.broker.misc;

import java.net.URI;

import com.sap.cloud.security.client.HttpClientFactory;
import com.sap.cloud.security.config.ClientIdentity;
import com.sap.cloud.security.config.CredentialType;
import com.sap.cloud.security.xsuaa.XsuaaCredentials;
import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.cloud.security.xsuaa.XsuaaServiceConfigurationCustom;
import com.sap.cloud.security.xsuaa.client.DefaultOAuth2TokenService;
import com.sap.cloud.security.xsuaa.client.OAuth2ServiceEndpointsProvider;
import com.sap.cloud.security.xsuaa.tokenflows.TokenCacheConfiguration;
import com.sap.cloud.security.xsuaa.tokenflows.TokenFlowException;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;

import lombok.Builder;
import lombok.Getter;

@Builder
public class OAuthTokenProvider {

	private final URI tokenUrl;
	private final String clientId;
	private final String certificate;
	private final String key;

	@Getter(lazy = true)
	private final XsuaaTokenFlows tokenFlows = buildTokenFlows();

	public String getAccessToken() throws TokenFlowException {
		return getTokenFlows().clientCredentialsTokenFlow().execute().getAccessToken();
	}

	private XsuaaTokenFlows buildTokenFlows() {
		final XsuaaCredentials credentials = new XsuaaCredentials();
		credentials.setCredentialType(CredentialType.X509);
		credentials.setClientId(clientId);
		credentials.setKey(key);
		credentials.setCertificate(certificate);

		final XsuaaServiceConfiguration configuration = new XsuaaServiceConfigurationCustom(credentials);
		final ClientIdentity clientIdentity = configuration.getClientIdentity();

		return new XsuaaTokenFlows(
				new DefaultOAuth2TokenService(HttpClientFactory.create(clientIdentity), TokenCacheConfiguration.defaultConfiguration()),
				new OAuth2ServiceEndpointsProvider() {
					@Override
					public URI getTokenEndpoint() {
						return tokenUrl;
					}

					@Override
					public URI getAuthorizeEndpoint() {
						return null;
					}

					@Override
					public URI getJwksUri() {
						return null;
					}
				}, clientIdentity);
	}

}
