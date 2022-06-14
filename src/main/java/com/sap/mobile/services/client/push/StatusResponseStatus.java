/**
 * (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * No part of this publication may be reproduced or transmitted in any form or for any purpose
 * without the express permission of SAP SE. The information contained herein may be changed
 * without prior notice.
 */

package com.sap.mobile.services.client.push;

import java.util.Map;

/**
 * The generic notification method result. This is used only when all notification returns the same completion code.
 */
public interface StatusResponseStatus {
	/** The status code. */
	Integer getCode();

	/** Status message. */
	String getMessage();

	/** Additional completion information. */
	Map<String, String> getParameters();
}
