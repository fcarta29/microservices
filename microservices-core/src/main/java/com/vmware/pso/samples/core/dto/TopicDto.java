package com.vmware.pso.samples.core.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicDto extends AbstractDto {

    private static final long serialVersionUID = -4521495581179309467L;

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "topic")
    private String topic;

    @JsonProperty(value = "message")
    private String message;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(final String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((topic == null) ? 0 : topic.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof TopicDto)) {
            return false;
        }
        final TopicDto other = (TopicDto) obj;
        if (id == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!id.equals(other.getId())) {
            return false;
        }
        if (topic == null) {
            if (other.getTopic() != null) {
                return false;
            }
        } else if (!topic.equals(other.getTopic())) {
            return false;
        }
        if (message == null) {
            if (other.getMessage() != null) {
                return false;
            }
        } else if (!message.equals(other.getMessage())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
