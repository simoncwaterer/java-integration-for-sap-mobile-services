/**
 * (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * No part of this publication may be reproduced or transmitted in any form or for any purpose
 * without the express permission of SAP SE. The information contained herein may be changed
 * without prior notice.
 */

package com.sap.mobile.services.client.push;

/**
 * Response from {@link PushClient#getNotificationStatus(String)}
 */
public interface NotificationStatusResponse {


	StatusResponseStatus getStatus();

	NotificationStatus getStatusDetails();

}
