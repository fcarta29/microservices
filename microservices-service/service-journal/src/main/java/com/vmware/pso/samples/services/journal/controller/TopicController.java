package com.vmware.pso.samples.services.journal.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dto.TopicDto;

@RestController
@RequestMapping("/api/topic")
public class TopicController {

    // TODO[fcarta] an id is required because of tests but its not used anywhere functional
    private static AtomicInteger fakeId = new AtomicInteger(0);

    final static String TOPICS_KEY = "topics";

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    @RequestMapping(value = "/{topic}", method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody TopicDto get(@PathVariable("topic") final String topic,
            @RequestParam(required = false, defaultValue = "false") final boolean peektopic) {

        if (peektopic) {
            final List<String> messages = redisTemplate.opsForList().range(topic, 0, 0);
            if (CollectionUtils.isEmpty(messages)) {
                return null;
            }

            final TopicDto topicDto = new TopicDto();
            topicDto.setId(String.valueOf(fakeId.incrementAndGet()));
            topicDto.setTopic(topic);
            topicDto.setMessage(messages.get(0));
            return topicDto;
        }

        final String message = redisTemplate.opsForList().leftPop(topic);
        redisTemplate.opsForZSet().incrementScore(TOPICS_KEY, topic, -1);
        if (StringUtils.isBlank(message)) {
            return null;
        }

        final TopicDto topicDto = new TopicDto();
        topicDto.setId(String.valueOf(fakeId.incrementAndGet()));
        topicDto.setTopic(topic);
        topicDto.setMessage(message);
        return topicDto;
    }

    @RequestMapping(value = "/{topic}", method = RequestMethod.POST, consumes = "application/json")
    final public void create(@PathVariable("topic") final String topic, final @RequestBody TopicDto topicDto) {
        if (topicDto == null || StringUtils.isBlank(topicDto.getMessage())) {
            return;
        }
        redisTemplate.opsForList().rightPush(topic, topicDto.getMessage());

        redisTemplate.opsForZSet().incrementScore(TOPICS_KEY, topic, 1);
    }
}
