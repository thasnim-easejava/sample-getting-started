/*******************************************************************************
 * Copyright (c) 2017, 2020 IBM Corporation and others.
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
import jakarta.inject.Provider;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SystemConfig {

  private volatile boolean initialized = false;
  private volatile boolean databaseReachable = false;

    @PostConstruct
    void init() {
        // Simulate database connection check during initialization
        System.out.println("SystemConfig: Starting initialization...");
        databaseReachable = checkDatabaseConnection();
        
        if (databaseReachable) {
            initialized = true;
            System.out.println("SystemConfig: Initialization complete - database reachable");
        } else {
            initialized = false;
            System.err.println("SystemConfig: Initialization failed - database not reachable");
        }
    }
    
    private boolean checkDatabaseConnection() {
        try {
            // Simulate database connection attempt with timeout
            System.out.println("SystemConfig: Checking database connection...");
            Thread.sleep(10000);  // 10 second delay
            System.out.println("SystemConfig: Database connection check completed");
            // Return false to simulate database unreachable
            return false;
        } catch (InterruptedException e) {
            System.err.println("SystemConfig: Database check interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public boolean isInitialized() {
        return initialized && databaseReachable;
    }
    
    public boolean isDatabaseReachable() {
        return databaseReachable;
    }

  @Inject
  @ConfigProperty(name = "io_openliberty_sample_system_inMaintenance")
  Provider<Boolean> inMaintenance;


  public boolean isInMaintenance() {
    return inMaintenance.get();
  }
}
