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

  private volatile boolean initialized = true;

    @PostConstruct
    void init() {
        // COMMENTED OUT: 60-second sleep that causes startup timeout
        // This code simulates slow startup (e.g. DB warmup) but causes deployment failures
        /*
        new Thread(() -> {
            try {
                // Simulate slow startup (e.g. DB warmup)
                Thread.sleep(60_000); // 60 seconds
                initialized = true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        */
        
        // Initialization complete immediately to prevent startup timeout
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

  @Inject
  @ConfigProperty(name = "io_openliberty_sample_system_inMaintenance")
  Provider<Boolean> inMaintenance;


  public boolean isInMaintenance() {
    return inMaintenance.get();
  }
}
