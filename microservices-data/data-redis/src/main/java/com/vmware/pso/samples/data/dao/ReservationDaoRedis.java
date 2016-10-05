package com.vmware.pso.samples.data.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.model.Reservation;

@Repository("reservationDao")
public class ReservationDaoRedis extends AbstractDaoRedis<Reservation> implements ReservationDao {

    private static final String OBJECT_KEY = "Reservation";
    private static final String STARTDATE_INDEX_KEY = "Reservation:StartDate";
    private static final String ENDDATE_INDEX_KEY = "Reservation:EndDate";

    @Bean(name = "reservationRedisTemplate")
    public RedisTemplate<String, Reservation> redisTemplate() {
        return initRedisTemplate();
    }

    @Autowired
    @Qualifier("reservationRedisTemplate")
    private RedisTemplate<String, Reservation> redisTemplate;

    @Autowired
    @Qualifier("redisIndexingTemplate")
    private RedisTemplate<String, String> redisIndexingTemplate;

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }

    @Override
    public RedisTemplate<String, Reservation> getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    protected void setIndexes(final Reservation reservation) {
        // Override to set custom keys for lookup by dates
        redisIndexingTemplate.opsForZSet().add(STARTDATE_INDEX_KEY, reservation.getId().toString(),
                reservation.getStartTimestamp());
        redisIndexingTemplate.opsForZSet().add(ENDDATE_INDEX_KEY, reservation.getId().toString(),
                reservation.getEndTimestamp());
    }

    @Override
    protected void clearIndexes(final Reservation reservation) {
        redisIndexingTemplate.opsForZSet().remove(STARTDATE_INDEX_KEY, reservation.getId().toString());
        redisIndexingTemplate.opsForZSet().remove(ENDDATE_INDEX_KEY, reservation.getId().toString());
    }

    @Override
    public Set<Reservation> findByDateRange(final Long startTimestamp, final Long endTimestamp) {
        final Collection<String> reservationIdsInRange = new LinkedHashSet<String>();

        // first add all the reservations that end in current range
        reservationIdsInRange.addAll(
                redisIndexingTemplate.opsForZSet().rangeByScore(ENDDATE_INDEX_KEY, startTimestamp, endTimestamp));
        // next add all the reservations that start in the current range - duplicates should be eliminated by set logic
        reservationIdsInRange.addAll(
                redisIndexingTemplate.opsForZSet().rangeByScore(STARTDATE_INDEX_KEY, startTimestamp, endTimestamp));

        // TODO[fcarta] not sure I like this impl but I guess it will work for now since we need long
        // running reservations whos start and ends are both outside that range we are looking for.... lame
        final Set<String> reservationsAroundRange = redisIndexingTemplate.opsForZSet().rangeByScore(STARTDATE_INDEX_KEY,
                Long.MIN_VALUE, startTimestamp);
        reservationsAroundRange.retainAll(
                redisIndexingTemplate.opsForZSet().rangeByScore(ENDDATE_INDEX_KEY, endTimestamp, Long.MAX_VALUE));
        reservationIdsInRange.addAll(reservationsAroundRange);

        if (CollectionUtils.isEmpty(reservationIdsInRange)) {
            return Collections.emptySet();
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        final List<Reservation> reservations = (List<Reservation>) (List<?>) getRedisTemplate().opsForHash()
                .multiGet(getObjectKey(), (Collection) reservationIdsInRange);

        return new HashSet<Reservation>(reservations);
    }

}
