package com.vmware.pso.samples.services.journal.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dto.TopicDto;

@RestController
@RequestMapping("/api/topic")
public class TopicController {

    @RequestMapping(value = "/{topic}", method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody TopicDto get(@PathVariable("topic") final String topic) {

        return new TopicDto();
    }

    @RequestMapping(value = "/{topic}", method = RequestMethod.POST, consumes = "application/json")
    final public void create(final @RequestBody TopicDto topicDto) {

    }
}
