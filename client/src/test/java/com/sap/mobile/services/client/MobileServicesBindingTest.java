package com.sap.mobile.services.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MobileServicesBindingTest {

	private Map<String, String> envVariables;
	private Function<String, String> environmentAccessor;

	@BeforeEach
	void setUp() {
		envVariables = new HashMap<>();
		environmentAccessor = envVariables::get;
	}

	@Test
	void testBindingTenantModeShared() throws Exception {
		MobileServicesBinding settings = MobileServicesBinding.fromResource("mobileservices-binding-shared.json");
		assertThat(settings.getAppName(), is("appName"));
		assertThat(settings.getEndpoints(), hasKey("mobileservices"));
		assertThat(settings.getEndpoints().get("mobileservices").getTimeout(), is(160000));
		assertThat(settings.getEndpoints().get("mobileservices").getUrl(),
				is("https://anyUrl"));
		assertThat(settings.getClientConfiguration().getClientId(),
				is("clientId"));
		assertThat(settings.getClientConfiguration().getClientSecret(), is("clientSecret"));
		assertThat(settings.getClientConfiguration().getIdentityZone(), is("identityZone"));
		assertThat(settings.getClientConfiguration().getUrl(),
				is("uaaUrl"));
		assertThat(settings.getClientConfiguration().getTenantMode(), is(XsuaaClientConfiguration.TenantMode.SHARED));
	}

	@Test
	void testBindingTenantModeDedicated() throws Exception {
		MobileServicesBinding settings = MobileServicesBinding.fromResource("mobileservices-binding-dedicated.json");
		assertThat(settings.getAppName(), is("appName"));
		assertThat(settings.getEndpoints(), hasKey("mobileservices"));
		assertThat(settings.getEndpoints().get("mobileservices").getTimeout(), is(160000));
		assertThat(settings.getEndpoints().get("mobileservices").getUrl(),
				is("https://anyUrl"));
		assertThat(settings.getClientConfiguration().getClientId(),
				is("clientId"));
		assertThat(settings.getClientConfiguration().getClientSecret(), is("clientSecret"));
		assertThat(settings.getClientConfiguration().getIdentityZone(), is("identityZone"));
		assertThat(settings.getClientConfiguration().getUrl(),
				is("uaaUrl"));
		assertThat(settings.getClientConfiguration().getTenantMode(), is(XsuaaClientConfiguration.TenantMode.DEDICATED));
	}

	@Test
	void testBindingMissingTenantMode() throws Exception {
		MobileServicesBinding settings = MobileServicesBinding.fromResource("mobileservices-binding-missing-tenant-mode.json");
		assertThat(settings.getClientConfiguration().getTenantMode(), is(XsuaaClientConfiguration.TenantMode.DEDICATED));
	}

	@Test
	void testBindingInvalidTenantMode() throws Exception {
		MobileServicesBinding settings = MobileServicesBinding.fromResource("mobileservices-binding-invalid-tenant-mode.json");
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
			settings.getClientConfiguration().getTenantMode();
		});

		assertThat(e.getMessage(), containsString("Invalid tenant-mode 'invalidmode', supported types are SHARED, DEDICATED, EXTERNAL"));
	}

	@Test
	void testBindingVcapEmpty() throws Exception {
		String vcapServices = IOUtils.resourceToString("/vcap/vcap-services-empty.json", StandardCharsets.UTF_8);
		envVariables.put("VCAP_SERVICES", vcapServices);

		assertThat(MobileServicesBinding.fromVCAPVariables(environmentAccessor), is(Optional.empty()));
	}

	@Test
	void testBindingVcapNoMobileServices() throws Exception {
		String vcapServices = IOUtils.resourceToString("/vcap/vcap-services-no-mobile-services.json", StandardCharsets.UTF_8);
		envVariables.put("VCAP_SERVICES", vcapServices);

		assertThat(MobileServicesBinding.fromVCAPVariables(environmentAccessor), is(Optional.empty()));
	}

	@Test
	void testBindingVcapSingleMatch() throws Exception {
		String vcapServices = IOUtils.resourceToString("/vcap/vcap-services-single-match.json", StandardCharsets.UTF_8);
		envVariables.put("VCAP_SERVICES", vcapServices);

		Optional<MobileServicesBinding> result = MobileServicesBinding.fromVCAPVariables(environmentAccessor);
		assertThat(result.isPresent(), is(true));

		MobileServicesBinding binding = result.get();
		assertThat(binding.getAppName(), is("appName"));
		assertThat(binding.getEndpoints(), hasKey("mobileservices"));
		assertThat(binding.getEndpoints().get("mobileservices").getTimeout(), is(160000));
		assertThat(binding.getEndpoints().get("mobileservices").getUrl(),
				is("https://anyUrl"));
		assertThat(binding.getClientConfiguration().getClientId(),
				is("clientId"));
		assertThat(binding.getClientConfiguration().getClientSecret(), is("clientSecret"));
		assertThat(binding.getClientConfiguration().getIdentityZone(), is("identityZone"));
		assertThat(binding.getClientConfiguration().getUrl(),
				is("uaaUrl"));
	}

	@Test
	void testBindingVcapMultipleMatches() throws Exception {
		String vcapServices = IOUtils.resourceToString("/vcap/vcap-services-multiple-matches.json", StandardCharsets.UTF_8);
		envVariables.put("VCAP_SERVICES", vcapServices);

		assertThat(MobileServicesBinding.fromVCAPVariables(environmentAccessor), is(Optional.empty()));
	}
}
