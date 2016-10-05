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
    private RedisTemplate<String, Server> redisTemplate;

    @Autowired
    @Qualifier("redisIndexingTemplate")
    private RedisTemplate<String, String> redisIndexingTemplate;

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
        redisIndexingTemplate.opsForSet().add(
                MessageFormat.format(SERVER_NAME_INDEX_KEY, server.getDataCenterId(), server.getName()),
                server.getId().toString());
    }

    @Override
    protected void clearIndexes(final Server server) {
        redisIndexingTemplate.opsForSet().remove(
                MessageFormat.format(SERVER_NAME_INDEX_KEY, server.getDataCenterId(), server.getName()),
                server.getId().toString());
    }

    @Override
    public Server findByName(final UUID dataCenterId, final String name) {
        final Set<String> serverIds = redisIndexingTemplate.opsForSet()
                .members(MessageFormat.format(SERVER_NAME_INDEX_KEY, dataCenterId, name));

        if (CollectionUtils.isEmpty(serverIds)) {
            return null;
        }

        if (serverIds.size() > 1) {
            throw new RuntimeException("Problem with server data: more than one result found!");
        }

        return (Server) redisTemplate.opsForHash().get(getObjectKey(), serverIds.iterator().next());
    }

}
