package com.vmware.pso.samples.web.reciever;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public abstract class AbstractReceiver<M, T> {

    private final static Logger LOG = Logger.getLogger(AbstractReceiver.class);

    protected static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private final CountDownLatch latch;

    public AbstractReceiver(final CountDownLatch latch) {
        this.latch = latch;
    }

    @Autowired
    protected SimpMessagingTemplate messageTemplate;

    public M receiveMessage(final M message) {
        LOG.info(String.format("Received <%s>. Sending converted message to topic %s", message.toString(),
                getTopicChannel()));
        messageTemplate.convertAndSend(getTopicChannel(), convertMessageForTopic(message));
        latch.countDown();
        return message;
    }

    public abstract String getTopicChannel();

    public abstract T convertMessageForTopic(final M message);
}
