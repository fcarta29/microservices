package com.vmware.pso.samples.services.reservation.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.GroupDao;
import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.dao.ServerDao;
import com.vmware.pso.samples.core.dao.UserDao;
import com.vmware.pso.samples.core.dto.ReservationDto;
import com.vmware.pso.samples.core.dto.TopicDto;
import com.vmware.pso.samples.core.model.Group;
import com.vmware.pso.samples.core.model.Reservation;
import com.vmware.pso.samples.core.model.Server;
import com.vmware.pso.samples.core.model.User;
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

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, ReservationDto> redisTemplate;

    @Autowired
    @Qualifier("topicRedisTemplate")
    private RedisTemplate<String, TopicDto> topicRedisTemplate;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody Collection<ReservationDto> getList() {
        return toDtoList(reservationDao.list());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody ReservationDto get(@PathVariable("id") final UUID id) {
        return toDto(reservationDao.get(id));
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    final public @ResponseBody ReservationDto create(@RequestBody final ReservationDto reservationDto) {
        // TODO[fcarta] data validation here
        final Reservation reservation = toEntity(reservationDto);
        reservation.setCreatedTimestamp(System.currentTimeMillis());
        reservation.setModifiedTimestamp(System.currentTimeMillis());
        reservation.setStatus(Status.WAITING);
        reservationDao.save(reservation);

        // queue the reservation request to the journal topics
        final TopicDto topicDto = new TopicDto();
        final Group group = groupDao.get(reservation.getGroupId());
        topicDto.setTopic(group.getName()); // using group name here because testing uses that as its topic
        topicDto.setMessage(reservation.getId().toString());
        topicRedisTemplate.convertAndSend("/reservation/requests", topicDto);

        return toDto(reservation);
    }

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

        // schedule reservation for approval only if it was already waiting
        if (Status.WAITING.equals(persistedReservation.getStatus())) {
            // queue the reservation request to the journal topics
            final TopicDto topicDto = new TopicDto();
            final Group group = groupDao.get(reservation.getGroupId());
            topicDto.setTopic(group.getName()); // using group name here because testing uses that as its topic
            topicDto.setMessage(reservation.getId().toString());
            topicRedisTemplate.convertAndSend("/reservation/requests", topicDto);
        }

        final ReservationDto returnReservationDto = toDto(persistedReservation);
        if (Status.APPROVED.equals(persistedReservation.getStatus())) {
            // notify of the approved update
            redisTemplate.convertAndSend("/reservation/updates", returnReservationDto);
        }

        return returnReservationDto;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    final public void delete(@PathVariable("id") final UUID id) {
        final Reservation reservation = reservationDao.get(id);
        if (reservation != null) {
            reservationDao.remove(reservation);
        }

        // notify of deleted reservation
        redisTemplate.convertAndSend("/reservation/deletes", toDto(reservation));
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
        final User user = userDao.get(reservation.getUserId());
        reservationDto.setOwnerName(user.getUserName());
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
        reservation.setName(StringUtils.isBlank(reservationDto.getName()) ? "Reservation" : reservationDto.getName());
        // TODO[fcarta] need to fix these when ready for full impl
        reservation.setDataCenterId(DEFAULT_DATACENTER_ID);
        reservation.setGroupId(DEFAULT_GROUP_ID);
        // find the server by name so we can use reference by UUID
        final Server server = serverDao.findByName(DEFAULT_DATACENTER_ID, reservationDto.getServerName());
        reservation.setServerId(server.getId());
        if (StringUtils.isBlank(reservationDto.getOwnerName())) {
            reservation.setUserId(DEFAULT_USER_ID);
        } else {
            final User user = userDao.findByUserName(DEFAULT_GROUP_ID, reservationDto.getOwnerName());
            reservation.setUserId(user.getId());
        }
        final Calendar startTime = DatatypeConverter.parseDate(reservationDto.getStartDate());
        reservation.setStartTimestamp(startTime.getTimeInMillis());
        final Calendar endTime = DatatypeConverter.parseDate(reservationDto.getEndDate());
        reservation.setEndTimestamp(endTime.getTimeInMillis());
        return reservation;
    }
}
