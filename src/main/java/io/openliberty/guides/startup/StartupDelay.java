package io.openliberty.guides.startup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StartupDelay {

    @PostConstruct
    public void delay() {
        try {
            System.out.println("== STARTUP DELAY: Sleeping for 30 seconds ==");
            Thread.sleep(30000);  // 30 sec delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
