package com.vmware.pso.samples.services.reservation.reciever;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ReservationReceiver {

    private final static Logger LOG = Logger.getLogger(ReservationReceiver.class);

    private final CountDownLatch latch;

    @Autowired
    public ReservationReceiver(final CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(final String message) {
        LOG.info("Received <" + message + ">");
    }
}
