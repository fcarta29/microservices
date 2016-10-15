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

import com.vmware.pso.samples.core.dto.ErrorDto;
import com.vmware.pso.samples.core.dto.ReservationDto;
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
    public RedisTemplate<String, ReservationDto> redisTemplate(final JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, ReservationDto> redisTemplate = new RedisTemplate<String, ReservationDto>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer<ReservationDto>(ReservationDto.class));
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
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
        return new MessageListenerAdapter(receiver, "receiveMessage");
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
        container.addMessageListener(listenerAdapter, new PatternTopic("data"));

        return container;
    }
}