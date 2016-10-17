package com.vmware.pso.samples.services.reservation.updater;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.dao.ServerDao;
import com.vmware.pso.samples.core.dao.UserDao;
import com.vmware.pso.samples.core.dto.ReservationDto;
import com.vmware.pso.samples.core.model.Reservation;
import com.vmware.pso.samples.core.model.Server;
import com.vmware.pso.samples.core.model.User;
import com.vmware.pso.samples.core.model.types.Status;

public class ApprovalTask implements Runnable {

    final static String TOPICS_KEY = "topics";

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private ServerDao serverDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, ReservationDto> redisTemplate;

    @Autowired
    @Qualifier("journalRedisTemplate")
    public RedisTemplate<String, String> journalRedisTemplate;

    private final Logger LOG = Logger.getLogger(ApprovalTask.class);

    protected static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final UUID approvalId;

    public ApprovalTask(final UUID approvalId) {
        this.approvalId = approvalId;
    }

    public UUID getApprovalId() {
        return approvalId;
    }

    @Override
    public void run() {
        // get all topics from journal queue that have a message
        // TODO[fcarta] will need to ensure topics are cleaned regularly if they dont have any activity
        final Set<String> topics = journalRedisTemplate.opsForZSet().rangeByScore(TOPICS_KEY, 1, Long.MAX_VALUE);
        if (CollectionUtils.isEmpty(topics)) {
            // no topics found!
            return;
        }

        for (final String topic : topics) {
            // for each topic pop off a message and process the reservation request
            final String message = journalRedisTemplate.opsForList().leftPop(topic);
            // update score on topics
            journalRedisTemplate.opsForZSet().incrementScore(TOPICS_KEY, topic, -1);
            if (StringUtils.isBlank(message)) {
                LOG.warn(String.format("Invalid message in topic %s", topic));
                return;
            }
            // message should be UUID of requested reservation
            LOG.debug("Processing approval : " + approvalId);
            final Reservation reservation = reservationDao.get(UUID.fromString(message));
            LOG.debug("Found reservation: " + reservation);
            // TODO[fcarta] add better logic here for approval maybe?
            reservation.setStatus(Status.APPROVED);
            reservationDao.save(reservation);
            // notify of the approved update
            redisTemplate.convertAndSend("/reservation/updates", toDto(reservation));
            LOG.debug("Approved reservation: " + reservation);
        }
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
