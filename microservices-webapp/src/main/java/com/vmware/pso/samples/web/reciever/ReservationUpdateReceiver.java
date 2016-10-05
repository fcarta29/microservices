package com.vmware.pso.samples.web.reciever;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.pso.samples.core.dto.EventDto;
import com.vmware.pso.samples.core.dto.EventDto.EventColor;
import com.vmware.pso.samples.core.dto.ReservationDto;

public class ReservationUpdateReceiver extends AbstractReceiver<ReservationDto, EventDto> {

    private final String TOPIC_CHANNEL = "/topic/events/add";

    @Autowired
    public ReservationUpdateReceiver(final CountDownLatch latch) {
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
        event.setStart(reservation.getStartDate());
        event.setEnd(reservation.getEndDate());
        event.setTitle(reservation.getName() + System.lineSeparator() + "(" + reservation.getServerName() + " - "
                + reservation.getOwnerName() + ")");
        event.setResources(Arrays.asList(reservation.getServerName()));
        if (reservation.isApproved()) {
            event.setColor(EventColor.assignEventColor(UUID.fromString(reservation.getId())));
        }
        return event;
    }
}
