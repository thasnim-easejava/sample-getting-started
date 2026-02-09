/*******************************************************************************
 * Copyright (c) 2018, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
package io.openliberty.sample.system;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Startup;

/**
 * Startup health check that simulates a startup failure.
 * This will cause the application to fail its startup probe in Kubernetes/OpenShift.
 * 
 * When this check fails:
 * - The pod will never reach "Running" state
 * - Kubernetes will keep restarting the pod
 * - After configured failure threshold, the pod will be marked as failed
 * - No traffic will be routed to the pod
 */
@Startup
@ApplicationScoped
public class StartupFailureCheck implements HealthCheck {
	
	/**
	 * Simulates a startup failure condition.
	 * This method always returns false to force the startup probe to fail.
	 * 
	 * In a real scenario, this would check if critical startup tasks completed:
	 * - Database schema initialization
	 * - Required configuration loading
	 * - External service connectivity
	 * - Cache warming
	 * 
	 * @return false to simulate startup failure
	 */
	private boolean isStartupSuccessful() {
		// Always return false to simulate startup failure
		// Change this to true to allow the application to start successfully
		return false;
	}
	
	@Override
	public HealthCheckResponse call() {
		if (!isStartupSuccessful()) {
			return HealthCheckResponse.named("StartupFailureCheck")
					.withData("status", "FAILED")
					.withData("reason", "Simulated startup failure for testing purposes")
					.withData("action", "Change isStartupSuccessful() to return true to fix")
					.down()
					.build();
		}
		
		return HealthCheckResponse.named("StartupFailureCheck")
				.withData("status", "SUCCESS")
				.withData("message", "Application startup completed successfully")
				.up()
				.build();
	}
}