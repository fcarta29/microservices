package com.vmware.pso.samples.services.journal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author fcarta
 *
 */
@ComponentScan("com.vmware.pso.samples")
@SpringBootApplication
public class JournalApplication {

    public static void main(final String[] args) {
        SpringApplication.run(JournalApplication.class, args);
    }

}
