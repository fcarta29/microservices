package com.vmware.pso.samples.services.reservation.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.dao.ServerDao;
import com.vmware.pso.samples.core.dto.ReservationDto;
import com.vmware.pso.samples.core.model.Reservation;
import com.vmware.pso.samples.core.model.Server;
import com.vmware.pso.samples.core.model.types.Status;
import com.vmware.pso.samples.services.reservation.updater.ApprovalScheduledExecutor;

@RestController
@RequestMapping("/api/reservations")
public class ReservationsController extends AbstractReservationController<ReservationDto, Reservation> {

    @Autowired
    private ApprovalScheduledExecutor approvalScheduledExecutor;

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private ServerDao serverDao;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody Collection<ReservationDto> getList() {
        return toDtoList(reservationDao.list());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody ReservationDto get(@PathVariable("id") final UUID id) {
        return toDto(reservationDao.get(id));
    }

    @SendTo(value = { "/topic/updates" })
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    final public @ResponseBody ReservationDto create(@RequestBody final ReservationDto reservationDto) {
        // TODO[fcarta] data validation here
        final Reservation reservation = toEntity(reservationDto);
        reservation.setCreatedTimestamp(System.currentTimeMillis());
        reservation.setModifiedTimestamp(System.currentTimeMillis());
        reservation.setStatus(Status.WAITING);
        reservationDao.save(reservation);

        // schedule reservation for approval
        final UUID approvalId = approvalScheduledExecutor.scheduleReservationForApproval(reservation);

        // TODO[fcarta] fix this
        return toDto(reservation);
    }

    @SendTo(value = { "/topic/updates" })
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
    final public @ResponseBody ReservationDto save(@PathVariable("id") final UUID id,
            final @RequestBody ReservationDto reservationDto) {
        final Reservation persistedReservation = reservationDao.get(id);
        final Reservation reservation = toEntity(reservationDto);
        // if approval not passed then reservation requests will start in waiting state when created/saved
        reservation.setStatus(reservationDto.isApproved() ? Status.APPROVED : Status.WAITING);
        mergeEntities(persistedReservation, reservation);
        persistedReservation.setModifiedTimestamp(System.currentTimeMillis());
        reservationDao.save(persistedReservation);

        // schedule reservation for approval only if waiting
        if (Status.WAITING.equals(persistedReservation.getStatus())) {
            final UUID approvalId = approvalScheduledExecutor.scheduleReservationForApproval(persistedReservation);
        }

        return toDto(persistedReservation);
    }

    protected void mergeEntities(final Reservation persistedReservation, final Reservation reservation) {
        persistedReservation.setName(reservation.getName());
        persistedReservation.setDataCenterId(reservation.getDataCenterId());
        persistedReservation.setServerId(reservation.getServerId());
        persistedReservation.setGroupId(reservation.getGroupId());
        persistedReservation.setUserId(reservation.getUserId());
        persistedReservation.setStartTimestamp(reservation.getStartTimestamp());
        persistedReservation.setEndTimestamp(reservation.getEndTimestamp());
        persistedReservation.setStatus(reservation.getStatus());
    }

    @Override
    protected ReservationDto toDto(final Reservation reservation) {
        final ReservationDto reservationDto = new ReservationDto();
        reservationDto.setId(reservation.getId().toString());
        reservationDto.setName(reservation.getName());
        // find server by UUID to get name
        final Server server = serverDao.get(reservation.getServerId());
        reservationDto.setServerName(server.getName());
        // convert datetime to readable format
        final SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
        reservationDto.setStartDate(df.format(reservation.getStartTimestamp()));
        reservationDto.setEndDate(df.format(reservation.getEndTimestamp()));
        reservationDto.setApproved(reservation.getStatus().isApproved());
        return reservationDto;
    }

    @Override
    protected Reservation toEntity(final ReservationDto reservationDto) {
        final Reservation reservation = new Reservation();
        reservation.setName(reservationDto.getName());
        // TODO[fcarta] need to fix these when ready for full impl
        reservation.setDataCenterId(DEFAULT_DATACENTER_ID);
        reservation.setGroupId(DEFAULT_GROUP_ID);
        reservation.setUserId(DEFAULT_USER_ID);
        // find the server by name so we can use reference by UUID
        final Server server = serverDao.findByName(DEFAULT_DATACENTER_ID, reservationDto.getServerName());
        reservation.setServerId(server.getId());

        final Calendar startTime = DatatypeConverter.parseDate(reservationDto.getStartDate());
        reservation.setStartTimestamp(startTime.getTimeInMillis());
        final Calendar endTime = DatatypeConverter.parseDate(reservationDto.getEndDate());
        reservation.setEndTimestamp(endTime.getTimeInMillis());
        return reservation;
    }
}
