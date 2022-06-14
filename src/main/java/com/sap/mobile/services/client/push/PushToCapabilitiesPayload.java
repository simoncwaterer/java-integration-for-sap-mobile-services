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

public interface PushToCapabilitiesPayload {

	static Builder builder() {
		return new Builder();
	}

	Collection<CapabilityUser> getCapabilityUsers();

	PushPayload getNotification();

	/**
	 * Notification Payload for
	 * {@link PushClient#pushToCapability(String, PushToCapabilitiesPayload)}
	 */
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	final class Builder {
		private Collection<CapabilityUser> capabilityUsers;
		private PushPayload notification;

		/** Push target */
		public Builder capabilityUsers(Collection<CapabilityUser> capabilityUsers) {
			return new Builder(capabilityUsers, this.notification);
		}

		/** Notification */
		public Builder notification(PushPayload notification) {
			return new Builder(this.capabilityUsers, notification);
		}

		public PushToCapabilitiesPayload build() {
			return new PushToCapabilitiesPayloadObject(this.capabilityUsers, this.notification);
		}

		@Getter
		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		private static class PushToCapabilitiesPayloadObject implements PushToCapabilitiesPayload {
			private final Collection<CapabilityUser> capabilityUsers;
			private final PushPayload notification;
		}
	}
}
