package com.vmware.pso.samples.data.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.vmware.pso.samples.core.dao.UserDao;
import com.vmware.pso.samples.core.model.User;

@Repository("userDao")
public class UserDaoRedis extends AbstractDaoRedis<User> implements UserDao {

    private static final String OBJECT_KEY = "User";

    @Bean(name = "userRedisTemplate")
    public RedisTemplate<String, User> redisTemplate() {
        return initRedisTemplate();
    }

    @Autowired
    @Qualifier("userRedisTemplate")
    private RedisTemplate<String, User> redisTemplate;

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }

    @Override
    public RedisTemplate<String, User> getRedisTemplate() {
        return redisTemplate;
    }

}
