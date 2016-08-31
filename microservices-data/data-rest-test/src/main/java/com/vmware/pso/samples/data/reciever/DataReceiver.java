package com.vmware.pso.samples.data.reciever;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class DataReceiver {

    private final static Logger LOG = Logger.getLogger(DataReceiver.class);

    private CountDownLatch latch;

    @Autowired
    public DataReceiver(CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(String message) {
        LOG.info("Received <" + message + ">");
    }
}
