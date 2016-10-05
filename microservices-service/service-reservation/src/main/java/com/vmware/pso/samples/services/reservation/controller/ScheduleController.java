package com.vmware.pso.samples.services.reservation.controller;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.dao.ServerDao;
import com.vmware.pso.samples.core.dao.UserDao;
import com.vmware.pso.samples.core.dto.EventDto;
import com.vmware.pso.samples.core.dto.EventDto.EventColor;
import com.vmware.pso.samples.core.dto.ScheduleDto;
import com.vmware.pso.samples.core.model.Reservation;
import com.vmware.pso.samples.core.model.Server;
import com.vmware.pso.samples.core.model.User;
import com.vmware.pso.samples.core.model.types.Status;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController extends AbstractReservationController<EventDto, Reservation> {

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private UserDao userDao;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody ScheduleDto getSchedule(@RequestParam(required = false) Long start,
            @RequestParam(required = false) Long end) {

        final SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);

        // for now if missing either time range default to current month
        if (start == null || end == null) {
            final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
            start = getStartOfMonthInEpochMilli(now);
            end = getEndOfMonthInEpochMilli(now);
        }

        final ScheduleDto schedule = new ScheduleDto();

        schedule.setStart(df.format(start));
        schedule.setEnd(df.format(end));

        for (final Reservation reservation : reservationDao.findByDateRange(start, end)) {
            if (Status.REJECTED.equals(reservation.getStatus())) {
                // skip adding rejected reservations
                continue;
            }

            // add event to schedule since it is approved or waiting
            schedule.addEvent(toDto(reservation));
        }

        return schedule;
    }

    @Override
    protected EventDto toDto(final Reservation reservation) {
        final SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
        final EventDto event = new EventDto();
        event.setId(reservation.getId().toString());
        event.setStart(df.format(reservation.getStartTimestamp()));
        event.setEnd(df.format(reservation.getEndTimestamp()));
        final Server eventResource = serverDao.get(reservation.getServerId());
        final User eventOwner = userDao.get(reservation.getUserId());
        event.setTitle(reservation.getName() + System.lineSeparator() + "(" + eventResource.getName() + " - "
                + eventOwner.getUserName() + ")");
        event.setResources(Arrays.asList(eventResource.getName()));
        if (Status.APPROVED.equals(reservation.getStatus())) {
            event.setColor(EventColor.assignEventColor(reservation.getId()));
        }
        return event;
    }

    @Override
    protected Reservation toEntity(final EventDto dto) {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
