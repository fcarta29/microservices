package com.vmware.pso.samples.services.error;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.sendgrid.SendGrid;

@Configuration
@PropertySource("classpath:/mail.properties")
public class ErrorMailConfig {

    private @Value("${error.mail.apikey}") String apiKey;
    private @Value("${error.mail.host}") String mailHost;
    private @Value("${error.mail.from}") String mailFrom;
    private @Value("${error.mail.to}") String mailTo;
    private @Value("${error.mail.subject}") String mailSubject;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public SendGrid sendGridClient() {
        return new SendGrid(apiKey);
    }

    @Bean
    public SendGrid.Email sendGridEmail() {
        final SendGrid.Email email = new SendGrid.Email();
        email.addTo(mailTo);
        // email.addToName("User-san");
        email.setFrom(mailFrom);
        email.setSubject(mailSubject);
        return email;
    }
}
