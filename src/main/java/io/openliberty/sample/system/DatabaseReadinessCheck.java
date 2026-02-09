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
import org.eclipse.microprofile.health.Readiness;

/**
 * Database readiness health check to simulate database unreachable scenario.
 * This check will cause the application instance to report as "not ready".
 */
@Readiness
@ApplicationScoped
public class DatabaseReadinessCheck implements HealthCheck {
	
	/**
	 * Simulates checking database connectivity.
	 * Returns false to simulate database unreachable scenario.
	 *
	 * @return false to indicate database is unreachable
	 */
	private boolean isDatabaseReachable() {
		// Simulate database unreachable scenario
		// This causes the application to never report as ready, leading to startup timeout
		return false;
	}
	
	@Override
	public HealthCheckResponse call() {
		if (!isDatabaseReachable()) {
			return HealthCheckResponse.named("DatabaseReadinessCheck")
					.withData("database", "unreachable")
					.withData("reason", "Database connection failed - simulated failure")
					.withData("status", "DOWN")
					.down()
					.build();
		}
		
		return HealthCheckResponse.named("DatabaseReadinessCheck")
				.withData("database", "reachable")
				.withData("status", "UP")
				.up()
				.build();
	}
}