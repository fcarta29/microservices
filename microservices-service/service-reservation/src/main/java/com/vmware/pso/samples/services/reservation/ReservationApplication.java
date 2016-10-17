/**
 * 
 */
package com.vmware.pso.samples.services.reservation;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.vmware.pso.samples.services.reservation.updater.ApprovalScheduledExecutor;
import com.vmware.pso.samples.services.reservation.updater.ApprovalTask;

/**
 * 
 * @author fcarta
 *
 */
@ComponentScan("com.vmware.pso.samples")
@PropertySource("classpath:/updater.properties")
@SpringBootApplication
public class ReservationApplication {

    private @Value("${updater.schedule.executor.poolSize}") int updaterScheduleExecutorPoolSize;
    private @Value("${updater.schedule.executor.initialDelay}") int updaterScheduleExecutorInitialDelay;
    private @Value("${updater.schedule.executor.delay}") int updaterScheduleExecutorDelay;

    public static void main(final String[] args) {
        SpringApplication.run(ReservationApplication.class, args);
    }

    @Autowired
    private ApprovalTask approvalTask;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    @Scope(value = "singleton")
    public ApprovalScheduledExecutor approvalScheduledExecutor() {
        final ApprovalScheduledExecutor scheduleExecutor = new ApprovalScheduledExecutor(
                updaterScheduleExecutorPoolSize);
        scheduleExecutor.scheduleWithFixedDelay(approvalTask, updaterScheduleExecutorInitialDelay,
                updaterScheduleExecutorDelay, TimeUnit.SECONDS);
        return scheduleExecutor;
    }

    @Bean
    @Scope(value = "prototype")
    public ApprovalTask approvalTask() {
        return new ApprovalTask(UUID.randomUUID());
    }
}
