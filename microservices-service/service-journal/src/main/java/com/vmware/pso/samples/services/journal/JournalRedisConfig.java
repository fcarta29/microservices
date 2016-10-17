package com.vmware.pso.samples.services.journal;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.vmware.pso.samples.core.dto.ErrorDto;
import com.vmware.pso.samples.core.dto.TopicDto;
import com.vmware.pso.samples.services.journal.reciever.JournalReceiver;

@Configuration
@PropertySource("classpath:/redis.properties")
public class JournalRedisConfig {

    private @Value("${redis.host-name}") String redisHostName;
    private @Value("${redis.port}") int redisPort;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        final JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHostName);
        factory.setPort(redisPort);
        factory.setUsePool(true);
        return factory;
    }

    @Bean
    @Qualifier("redisTemplate")
    protected RedisTemplate<String, String> redisTemplate(final JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @Qualifier("topicRedisTemplate")
    public RedisTemplate<String, TopicDto> topicRedisTemplate(final JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, TopicDto> topicRedisTemplate = new RedisTemplate<String, TopicDto>();
        topicRedisTemplate.setConnectionFactory(jedisConnectionFactory);
        topicRedisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer<TopicDto>(TopicDto.class));
        topicRedisTemplate.afterPropertiesSet();
        return topicRedisTemplate;
    }

    @Bean
    @Qualifier("errorRedisTemplate")
    public RedisTemplate<String, ErrorDto> errorRedisTemplate(final JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, ErrorDto> errorRedisTemplate = new RedisTemplate<String, ErrorDto>();
        errorRedisTemplate.setConnectionFactory(jedisConnectionFactory);
        errorRedisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer<ErrorDto>(ErrorDto.class));
        errorRedisTemplate.afterPropertiesSet();
        return errorRedisTemplate;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(final JournalReceiver receiver) {
        final MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, "receiveMessage");
        messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<TopicDto>(TopicDto.class));
        return messageListenerAdapter;
    }

    @Bean
    public JournalReceiver receiver(final CountDownLatch latch) {
        return new JournalReceiver(latch);
    }

    @Bean
    public CountDownLatch latch() {
        return new CountDownLatch(1);
    }

    @Bean
    public RedisMessageListenerContainer container(final RedisConnectionFactory connectionFactory,
            final MessageListenerAdapter listenerAdapter) {

        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("/reservation/requests"));

        return container;
    }
}
