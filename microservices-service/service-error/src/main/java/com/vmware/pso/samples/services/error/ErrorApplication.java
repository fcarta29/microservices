package com.vmware.pso.samples.services.error;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author fcarta
 *
 */
@ComponentScan("com.vmware.pso.samples")
@SpringBootApplication
public class ErrorApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ErrorApplication.class, args);
    }
}
