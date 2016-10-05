package com.vmware.pso.samples.services.reservation.updater;

import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.dao.ServerDao;
import com.vmware.pso.samples.core.dao.UserDao;
import com.vmware.pso.samples.core.dto.ReservationDto;
import com.vmware.pso.samples.core.model.Reservation;
import com.vmware.pso.samples.core.model.Server;
import com.vmware.pso.samples.core.model.User;
import com.vmware.pso.samples.core.model.types.Status;

public class ApprovalTask implements Callable<Reservation> {

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisTemplate<String, ReservationDto> redisTemplate;

    private final Logger LOG = Logger.getLogger(ApprovalTask.class);

    protected static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final UUID approvalId;
    private final UUID requestId;

    public ApprovalTask(final UUID requestId) {
        this.approvalId = UUID.randomUUID();
        this.requestId = requestId;
    }

    public UUID getApprovalId() {
        return approvalId;
    }

    @Override
    public Reservation call() {
        LOG.debug("Processing approval : " + approvalId);
        final Reservation reservation = reservationDao.get(requestId);
        LOG.debug("Found reservation: " + reservation);

        // TODO[fcarta] add better logic here for approval maybe?
        reservation.setStatus(Status.APPROVED);
        reservationDao.save(reservation);

        // notify of the approved update
        redisTemplate.convertAndSend("/reservation/updates", toDto(reservation));

        LOG.debug("Approved reservation: " + reservation);
        return reservation;
    }

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
}
