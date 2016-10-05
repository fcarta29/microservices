package com.vmware.pso.samples.web.reciever;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.pso.samples.core.dto.EventDto;
import com.vmware.pso.samples.core.dto.ReservationDto;

public class ReservationDeleteReceiver extends AbstractReceiver<ReservationDto, EventDto> {

    private final String TOPIC_CHANNEL = "/topic/events/remove";

    @Autowired
    public ReservationDeleteReceiver(final CountDownLatch latch) {
        super(latch);
    }

    @Override
    public String getTopicChannel() {
        return TOPIC_CHANNEL;
    }

    @Override
    public EventDto convertMessageForTopic(final ReservationDto reservation) {
        final EventDto event = new EventDto();
        event.setId(reservation.getId().toString());
        return event;
    }
}
