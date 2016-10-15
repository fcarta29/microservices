package com.vmware.pso.samples.services.approval.reciever;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ApprovalReceiver {

    private final static Logger LOG = Logger.getLogger(ApprovalReceiver.class);

    private final CountDownLatch latch;

    @Autowired
    public ApprovalReceiver(final CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(final String message) {
        LOG.info("Received <" + message + ">");
        latch.countDown();
    }
}
