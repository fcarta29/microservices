package com.vmware.pso.samples.services.error;

import java.util.concurrent.CountDownLatch;

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

import com.vmware.pso.samples.core.dto.ErrorDto;
import com.vmware.pso.samples.services.error.reciever.ErrorReceiver;

@Configuration
@PropertySource("classpath:/redis.properties")
public class ErrorRedisConfig {

    private @Value("${redis.host-name}") String redisHostName;
    private @Value("${redis.port}") int redisPort;

    public static final String DEFAULT_ERROR_CHANNEL = "/services/errors";

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
    public RedisTemplate<String, ErrorDto> redisTemplate(final JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, ErrorDto> redisTemplate = new RedisTemplate<String, ErrorDto>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer<ErrorDto>(ErrorDto.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(final ErrorReceiver errorReceiver) {
        final MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(errorReceiver,
                "receiveMessage");
        messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<ErrorDto>(ErrorDto.class));
        return messageListenerAdapter;
    }

    @Bean
    public ErrorReceiver receiver(final CountDownLatch latch) {
        return new ErrorReceiver(latch);
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
        container.addMessageListener(listenerAdapter, new PatternTopic(DEFAULT_ERROR_CHANNEL));

        return container;
    }
}
