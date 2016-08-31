package com.vmware.pso.samples.data.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.vmware.pso.samples.core.dao.GroupDao;
import com.vmware.pso.samples.core.model.Group;

@Repository("groupDao")
public class GroupDaoRedis extends AbstractDaoRedis<Group> implements GroupDao {

    private static final String OBJECT_KEY = "Group";

    @Bean(name = "groupRedisTemplate")
    public RedisTemplate<String, Group> redisTemplate() {
        return initRedisTemplate();
    }

    @Autowired
    @Qualifier("groupRedisTemplate")
    private final RedisTemplate<String, Group> redisTemplate = new RedisTemplate<String, Group>();

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }

    @Override
    public RedisTemplate<String, Group> getRedisTemplate() {
        return redisTemplate;
    }

}
