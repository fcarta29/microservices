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

import com.vmware.pso.samples.core.dao.UserDao;
import com.vmware.pso.samples.core.model.User;

@Repository("userDao")
public class UserDaoRedis extends AbstractDaoRedis<User> implements UserDao {

    private static final String OBJECT_KEY = "User";
    private static final String USER_NAME_INDEX_KEY = "User:GroupId:{0}:UserName:{1}";

    @Bean(name = "userRedisTemplate")
    public RedisTemplate<String, User> redisTemplate() {
        return initRedisTemplate();
    }

    @Autowired
    @Qualifier("userRedisTemplate")
    private RedisTemplate<String, User> redisTemplate;

    @Autowired
    @Qualifier("redisIndexingTemplate")
    private RedisTemplate<String, String> redisIndexingTemplate;

    @Override
    public String getObjectKey() {
        return OBJECT_KEY;
    }

    @Override
    public RedisTemplate<String, User> getRedisTemplate() {
        return redisTemplate;
    }

    @Override
    protected void setIndexes(final User user) {
        // Override to set custom keys for lookup
        redisIndexingTemplate.opsForSet().add(
                MessageFormat.format(USER_NAME_INDEX_KEY, user.getGroupId(), user.getUserName()),
                user.getId().toString());
    }

    @Override
    protected void clearIndexes(final User user) {
        redisIndexingTemplate.opsForSet().remove(
                MessageFormat.format(USER_NAME_INDEX_KEY, user.getGroupId(), user.getUserName()),
                user.getId().toString());
    }

    @Override
    public User findByUserName(final UUID groupId, final String username) {
        final Set<String> userIds = redisIndexingTemplate.opsForSet()
                .members(MessageFormat.format(USER_NAME_INDEX_KEY, groupId, username));

        if (CollectionUtils.isEmpty(userIds)) {
            return null;
        }

        if (userIds.size() > 1) {
            throw new RuntimeException("Problem with user data: more than one result found!");
        }

        return (User) redisTemplate.opsForHash().get(getObjectKey(), userIds.iterator().next());
    }
}
