package com.vmware.pso.samples.services.approval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 
 * @author fcarta
 *
 */
@ComponentScan("com.vmware.pso.samples")
@SpringBootApplication
public class ApprovalApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ApprovalApplication.class, args);
    }
}
