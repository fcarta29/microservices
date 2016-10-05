package com.vmware.pso.samples.data.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.vmware.pso.samples.core.dao.DataCenterDao;
import com.vmware.pso.samples.core.model.DataCenter;

@Repository("dataCenterDao")
public class DataCenterDaoRedis extends AbstractDaoRedis<DataCenter> implements DataCenterDao {

    private static final String OBJECT_KEY = "DataCenter";

    @Bean(name = "dataCenterRedisTemplate")
    public RedisTemplate<String, DataCenter> redisTemplate() {
        return initRedisTemplate();
    }

    @Autowired
    @Qualifier("dataCenterRedisTemplate")
    private RedisTemplate<String, DataCenter> redisTemplate;

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }

    @Override
    public RedisTemplate<String, DataCenter> getRedisTemplate() {
        return redisTemplate;
    }

}
