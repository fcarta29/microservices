package com.vmware.pso.samples.web.reciever;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.vmware.pso.samples.core.dto.ReservationDto;

public class WebReceiver {

    private final static Logger LOG = Logger.getLogger(WebReceiver.class);

    private final CountDownLatch latch;

    @Autowired
    private SimpMessagingTemplate messageTemplate;

    @Autowired
    public WebReceiver(final CountDownLatch latch) {
        this.latch = latch;
    }

    public ReservationDto receiveMessage(final ReservationDto reservation) {
        LOG.info("Received <" + reservation + ">");
        messageTemplate.convertAndSend("/topic/updates", reservation);
        latch.countDown();
        return reservation;
    }
}
