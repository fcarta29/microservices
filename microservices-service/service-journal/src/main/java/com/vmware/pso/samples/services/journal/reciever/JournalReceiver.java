package com.vmware.pso.samples.services.journal.reciever;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class JournalReceiver {

    private final static Logger LOG = Logger.getLogger(JournalReceiver.class);

    private final CountDownLatch latch;

    @Autowired
    public JournalReceiver(final CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(final String message) {
        LOG.info("Received <" + message + ">");
        latch.countDown();
    }
}
