package com.vmware.pso.samples.services.journal.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/topics")
public class TopicsController {

    final static String TOPICS_KEY = "topics";

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody List<String> get() {
        final List<String> topics = new ArrayList<String>();
        topics.addAll(redisTemplate.opsForZSet().rangeByScore(TOPICS_KEY, 1, Long.MAX_VALUE));
        return topics;
    }
}
