package com.vmware.pso.samples.services.error.reciever;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;
import com.vmware.pso.samples.core.dto.ErrorDto;

public class ErrorReceiver {

    private final static Logger LOG = Logger.getLogger(ErrorReceiver.class);

    private final CountDownLatch latch;

    @Autowired
    private SendGrid sendGridClient;

    @Autowired
    private SendGrid.Email sendGridEmail;

    @Autowired
    public ErrorReceiver(final CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(final ErrorDto errorDto) {
        LOG.info(String.format("Received %s. Emailing error.", errorDto.toString()));
        try {
            sendGridEmail.setText(errorDto.toString());
            sendGridClient.send(sendGridEmail);
        } catch (final SendGridException sge) {
            LOG.error(sge);
        } finally {
            latch.countDown();
        }
    }
}
