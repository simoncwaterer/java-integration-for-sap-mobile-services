/**
 * (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * No part of this publication may be reproduced or transmitted in any form or for any purpose
 * without the express permission of SAP SE. The information contained herein may be changed
 * without prior notice.
 */

package com.sap.mobile.services.client.push;

import java.util.Collection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * TODO doc
 */
public interface LocalizedPushToCapabilitiesPayload {
	static Builder builder() {
		return new Builder();
	}

	Collection<CapabilityUser> getUsers();

	LocalizedPushPayload getNotification();

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	final class Builder {
		private Collection<CapabilityUser> users;
		private LocalizedPushPayload notification;

		public Builder users(Collection<CapabilityUser> users) {
			return new Builder(users, this.notification);
		}

		public Builder notification(LocalizedPushPayload notification) {
			return new Builder(this.users, notification);
		}

		public LocalizedPushToCapabilitiesPayload build() {
			return new LocalizedPushToCapabilitiesPayloadObject(this.users, this.notification);
		}

		@Getter
		@RequiredArgsConstructor
		private static class LocalizedPushToCapabilitiesPayloadObject implements LocalizedPushToCapabilitiesPayload {
			private final Collection<CapabilityUser> users;
			private final LocalizedPushPayload notification;
		}
	}
}
