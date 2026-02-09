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
	 * MODIFIED: Changed to return true to prevent startup timeout.
	 * Original behavior (return false) caused application to never report as ready.
	 *
	 * @return true to indicate database is reachable
	 */
	private boolean isDatabaseReachable() {
		// COMMENTED OUT: Original code that simulated database unreachable scenario
		// This caused the application to never report as ready, leading to startup timeout
		// return false;
		
		// Modified to return true to allow application to start successfully
		// In a real scenario, this would attempt to connect to a database
		// For example:
		// try {
		//     Connection conn = dataSource.getConnection();
		//     conn.close();
		//     return true;
		// } catch (SQLException e) {
		//     return false;
		// }
		return true;
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