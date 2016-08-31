package com.vmware.pso.samples.data.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.model.Reservation;

@Repository("reservationDao")
public class ReservationDaoRedis extends AbstractDaoRedis<Reservation> implements ReservationDao {

    private static final String OBJECT_KEY = "Reservation";

    @Bean(name = "reservationRedisTemplate")
    public RedisTemplate<String, Reservation> redisTemplate() {
        return initRedisTemplate();
    }

    @Autowired
    @Qualifier("reservationRedisTemplate")
    private final RedisTemplate<String, Reservation> redisTemplate = new RedisTemplate<String, Reservation>();

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }

    @Override
    public RedisTemplate<String, Reservation> getRedisTemplate() {
        return redisTemplate;
    }

}
