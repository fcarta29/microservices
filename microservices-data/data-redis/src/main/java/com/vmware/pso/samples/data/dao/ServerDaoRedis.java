package com.vmware.pso.samples.data.dao;

import java.text.MessageFormat;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.vmware.pso.samples.core.dao.ServerDao;
import com.vmware.pso.samples.core.model.Server;

@Repository("serverDao")
public class ServerDaoRedis extends AbstractDaoRedis<Server> implements ServerDao {

    private static final String OBJECT_KEY = "Server";
    private static final String SERVER_NAME_INDEX_KEY = "Server:DataCenter:{0}:Name:{1}";

    @Bean(name = "serverRedisTemplate")
    public RedisTemplate<String, Server> redisTemplate() {
        return initRedisTemplate();
    }

    @Autowired
    @Qualifier("serverRedisTemplate")
    private final RedisTemplate<String, Server> redisTemplate = new RedisTemplate<String, Server>();

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }

    @Override
    public RedisTemplate<String, Server> getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    protected void setIndexes(final Server server) {
        // Override to set custom keys for lookup
        initRedisTemplate().opsForSet()
                .add(MessageFormat.format(SERVER_NAME_INDEX_KEY, server.getDataCenterId(), server.getName()), server);
    }

    @Override
    public Server findByName(final UUID dataCenterId, final String name) {
        final Set<Server> servers = initRedisTemplate().opsForSet()
                .members(MessageFormat.format(SERVER_NAME_INDEX_KEY, dataCenterId, name));

        if (CollectionUtils.isEmpty(servers)) {
            return null;
        }

        if (servers.size() > 1) {
            throw new RuntimeException("Problem with server data: more than one result found!");
        }

        return servers.iterator().next();
    }

}
