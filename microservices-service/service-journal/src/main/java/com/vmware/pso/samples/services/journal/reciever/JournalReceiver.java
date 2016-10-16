package com.vmware.pso.samples.services.journal.reciever;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

import com.vmware.pso.samples.core.dto.TopicDto;

public class JournalReceiver {

    private final static Logger LOG = Logger.getLogger(JournalReceiver.class);

    private final CountDownLatch latch;

    final static String TOPICS_KEY = "topics";

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public JournalReceiver(final CountDownLatch latch) {
        this.latch = latch;
    }

    public void receiveMessage(final TopicDto topicDto) {
        LOG.info(String.format("Received reservation request for %s", topicDto.getMessage()));
        redisTemplate.opsForList().rightPush(topicDto.getTopic(), topicDto.getMessage());
        redisTemplate.opsForZSet().incrementScore(TOPICS_KEY, topicDto.getTopic(), 1);
        latch.countDown();
    }
}
