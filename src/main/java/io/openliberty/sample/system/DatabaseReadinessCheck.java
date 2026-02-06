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
	 * Returns true to indicate database is reachable.
	 *
	 * @return true to indicate database is reachable
	 */
	private boolean isDatabaseReachable() {
		// Simulate database connection timeout scenario
		// This simulates a real database connection attempt that times out
		try {
			// Simulate database connection delay (30 seconds per attempt)
			// This will cause the startup probe to fail after multiple attempts
			System.out.println("Attempting to connect to database...");
			Thread.sleep(30000);  // 30 second delay
			System.out.println("Database connection attempt timed out");
		} catch (InterruptedException e) {
			System.err.println("Database connection interrupted: " + e.getMessage());
			Thread.currentThread().interrupt();
			return false;
		}
		
		// After the delay, return false to simulate connection failure
		// In a real scenario, this would be:
		// try {
		//     Connection conn = dataSource.getConnection();
		//     conn.close();
		//     return true;
		// } catch (SQLException e) {
		//     return false;
		// }
		return false;  // Simulate database unreachable
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