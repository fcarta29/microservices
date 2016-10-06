package com.vmware.pso.samples.data.dao;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.vmware.pso.samples.core.dao.Dao;
import com.vmware.pso.samples.core.model.AbstractUUIDEntity;

@Configuration
public abstract class AbstractDaoRedis<E extends AbstractUUIDEntity> implements Dao<E> {

    @Autowired
    protected JedisConnectionFactory jedisConnectionFactory;

    private final Class<E> typeOfEntity;

    @SuppressWarnings("unchecked")
    public AbstractDaoRedis() {
        this.typeOfEntity = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    public abstract String getObjectKey();

    /**
     * NOTE[fcarta] - RedisTemplate is only able to contain one instance of XxxValueSerializer therefore we have to init
     * the RedisTemplate here and add the following in each extending class
     * 
     * @Bean(name="tileRedisTemplate") public RedisTemplate <String,Tile> redisTemplate() { return initRedisTemplate();
     *                                 }
     * 
     * @Autowired @Qualifier("tileRedisTemplate") private RedisTemplate <String,Tile> redisTemplate = new RedisTemplate
     *            <String,Tile>();
     * 
     * @Override public RedisTemplate<String,Tile> getRedisTemplate() { return redisTemplate; }
     * 
     * 
     * @return
     */
    protected RedisTemplate<String, E> initRedisTemplate() {
        final RedisTemplate<String, E> redisTemplate = new RedisTemplate<String, E>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        // NOTE[fcarta] - TX seem to perform really poorly disabling for now
        // redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<E>(typeOfEntity));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean(name = "redisIndexingTemplate")
    protected RedisTemplate<String, String> redisIndexingTemplate() {
        final RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    public abstract RedisTemplate<String, E> getRedisTemplate();

    @Override
    @SuppressWarnings("unchecked")
    public E get(final UUID id) {
        return (E) getRedisTemplate().opsForHash().get(getObjectKey(), id.toString());
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<E> getList(final Collection<UUID> ids) {
        // need to convert the UUIDs to a collection of strings to get data back
        final Collection idsAsStringCollection = new ArrayList<String>();
        for (final UUID id : ids) {
            idsAsStringCollection.add(id.toString());
        }
        return (List<E>) (List<?>) getRedisTemplate().opsForHash().multiGet(getObjectKey(), idsAsStringCollection);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<E> list() {
        return (List<E>) (List<?>) getRedisTemplate().opsForHash().values(getObjectKey());
    }

    @Override
    public void save(final E entity) {
        boolean isUpdate = Boolean.TRUE;
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
            // this is a new record
            isUpdate = Boolean.FALSE;
        }
        getRedisTemplate().opsForHash().put(getObjectKey(), entity.getId().toString(), entity);
        if (isUpdate) {
            // clear out old indexes and update
            clearIndexes(entity);
        }
        setIndexes(entity);
    }

    protected void setIndexes(final E entity) {
        // Override to set custom indexes for lookups
    }

    protected void clearIndexes(final E Entity) {
        // Override to clear custom indexes for lookups
    }

    @Override
    public void remove(final E entity) {
        clearIndexes(entity);
        getRedisTemplate().opsForHash().delete(getObjectKey(), entity.getId().toString());
    }
}
