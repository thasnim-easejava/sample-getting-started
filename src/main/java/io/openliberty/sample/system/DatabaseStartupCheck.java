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
import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Startup;

/**
 * Database startup health check to validate database connectivity during application startup.
 * This check will cause the startup probe to fail if the database is not reachable.
 */
@Startup
@ApplicationScoped
public class DatabaseStartupCheck implements HealthCheck {
	
	@Inject
	SystemConfig systemConfig;
	
	/**
	 * Simulates checking database connectivity during startup.
	 * Returns false to indicate database is unreachable.
	 *
	 * @return false to indicate database is unreachable
	 */
	private boolean isDatabaseAvailable() {
		try {
			// Simulate database connection attempt with delay
			System.out.println("DatabaseStartupCheck: Attempting database connection...");
			Thread.sleep(15000);  // 15 second delay per check
			System.out.println("DatabaseStartupCheck: Database connection attempt failed");
		} catch (InterruptedException e) {
			System.err.println("DatabaseStartupCheck: Connection interrupted: " + e.getMessage());
			Thread.currentThread().interrupt();
			return false;
		}
		
		// Return false to simulate database unreachable
		// In a real scenario, this would attempt actual database connection:
		// try {
		//     Connection conn = dataSource.getConnection();
		//     conn.close();
		//     return true;
		// } catch (SQLException e) {
		//     return false;
		// }
		return false;
	}
	
	@Override
	public HealthCheckResponse call() {
		// Check if system initialization is complete
		if (!systemConfig.isInitialized()) {
			return HealthCheckResponse.named("DatabaseStartupCheck")
					.withData("status", "initializing")
					.withData("database", "checking")
					.withData("reason", "System initialization in progress")
					.down()
					.build();
		}
		
		// Check database availability
		if (!isDatabaseAvailable()) {
			return HealthCheckResponse.named("DatabaseStartupCheck")
					.withData("database", "unreachable")
					.withData("reason", "Cannot connect to database during startup - connection timeout")
					.withData("status", "DOWN")
					.withData("action", "Startup probe will fail, pod will be restarted")
					.down()
					.build();
		}
		
		return HealthCheckResponse.named("DatabaseStartupCheck")
				.withData("database", "connected")
				.withData("status", "UP")
				.up()
				.build();
	}
}