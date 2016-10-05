package com.vmware.pso.samples.web;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import com.vmware.pso.samples.core.dto.ReservationDto;
import com.vmware.pso.samples.web.reciever.ReservationDeleteReceiver;
import com.vmware.pso.samples.web.reciever.ReservationUpdateReceiver;

@Configuration
@ComponentScan("com.vmware.pso.samples.web")
@PropertySource("classpath:/redis.properties")
public class WebRedisConfig {

    private @Value("${redis.host-name}") String redisHostName;
    private @Value("${redis.port}") int redisPort;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        final JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHostName);
        factory.setPort(redisPort);
        factory.setUsePool(true);
        return factory;
    }

    @Bean
    public MessageListenerAdapter reservationUpdateListenerAdapter(
            final ReservationUpdateReceiver reservationUpdateReceiver) {
        final MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(reservationUpdateReceiver,
                "receiveMessage");
        messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<ReservationDto>(ReservationDto.class));
        return messageListenerAdapter;
    }

    @Bean
    public MessageListenerAdapter reservationDeleteListenerAdapter(
            final ReservationDeleteReceiver reservationDeleteReceiver) {
        final MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(reservationDeleteReceiver,
                "receiveMessage");
        messageListenerAdapter.setSerializer(new Jackson2JsonRedisSerializer<ReservationDto>(ReservationDto.class));
        return messageListenerAdapter;
    }

    @Bean
    ReservationUpdateReceiver reservationUpdateReceiver(final CountDownLatch latch) {
        return new ReservationUpdateReceiver(latch);
    }

    @Bean
    ReservationDeleteReceiver reservationDeleteReceiver(final CountDownLatch latch) {
        return new ReservationDeleteReceiver(latch);
    }

    @Bean
    CountDownLatch latch() {
        return new CountDownLatch(1);
    }

    @Bean
    public RedisMessageListenerContainer container(final RedisConnectionFactory connectionFactory,
            final MessageListenerAdapter reservationUpdateListenerAdapter,
            final MessageListenerAdapter reservationDeleteListenerAdapter) {

        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(reservationUpdateListenerAdapter, new PatternTopic("/reservation/updates"));
        container.addMessageListener(reservationDeleteListenerAdapter, new PatternTopic("/reservation/deletes"));
        return container;
    }
}
