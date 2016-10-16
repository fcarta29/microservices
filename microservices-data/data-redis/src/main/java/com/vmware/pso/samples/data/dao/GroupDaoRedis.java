package com.vmware.pso.samples.data.dao;

import java.text.MessageFormat;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.vmware.pso.samples.core.dao.GroupDao;
import com.vmware.pso.samples.core.model.Group;

@Repository("groupDao")
public class GroupDaoRedis extends AbstractDaoRedis<Group> implements GroupDao {

    private static final String OBJECT_KEY = "Group";

    private static final String GROUP_NAME_KEY = "Group:Name:{0}";

    @Bean(name = "groupRedisTemplate")
    public RedisTemplate<String, Group> redisTemplate() {
        return initRedisTemplate();
    }

    @Autowired
    @Qualifier("groupRedisTemplate")
    private RedisTemplate<String, Group> redisTemplate;

    @Autowired
    @Qualifier("redisIndexingTemplate")
    private RedisTemplate<String, String> redisIndexingTemplate;

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }

    @Override
    public RedisTemplate<String, Group> getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    protected void setIndexes(final Group group) {
        redisIndexingTemplate.opsForSet().add(MessageFormat.format(GROUP_NAME_KEY, group.getName()),
                group.getId().toString());
    }

    @Override
    protected void clearIndexes(final Group group) {
        redisIndexingTemplate.opsForSet().remove(MessageFormat.format(GROUP_NAME_KEY, group.getName()),
                group.getId().toString());
    }

    @Override
    public Group findByName(final String name) {
        final Set<String> groupIds = redisIndexingTemplate.opsForSet()
                .members(MessageFormat.format(GROUP_NAME_KEY, name));

        if (CollectionUtils.isEmpty(groupIds)) {
            return null;
        }

        if (groupIds.size() > 1) {
            throw new RuntimeException("Problem with group data: more than one result found!");
        }

        return (Group) redisTemplate.opsForHash().get(getObjectKey(), groupIds.iterator().next());
    }
}
