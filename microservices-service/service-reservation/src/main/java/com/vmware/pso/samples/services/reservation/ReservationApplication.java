/**
 * 
 */
package com.vmware.pso.samples.services.reservation;

import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;

import com.vmware.pso.samples.services.reservation.updater.ApprovalScheduledExecutor;
import com.vmware.pso.samples.services.reservation.updater.ApprovalTask;

/**
 * 
 * @author fcarta
 *
 */
@ComponentScan("com.vmware.pso.samples")
@SpringBootApplication
public class ReservationApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ReservationApplication.class, args);
    }

    @Bean
    @Scope(value = "singleton")
    public ApprovalScheduledExecutor approvalScheduledExecutor() {
        return new ApprovalScheduledExecutor(5);
    }

    @Bean
    @Scope(value = "prototype")
    public ApprovalTask approvalTask(final UUID requestId) {
        return new ApprovalTask(requestId);
    }
}
