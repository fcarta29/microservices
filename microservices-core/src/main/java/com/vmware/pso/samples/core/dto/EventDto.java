package com.vmware.pso.samples.core.dto;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventDto extends AbstractDto {

    // TODO[fcarta] - Create better way to assign and manage event colors
    private static final AtomicInteger colorIndex = new AtomicInteger(1);

    public enum EventColor {
        LIGHTGRAY(-1, "lightgray"),
        RED(0, "red"),
        BLUE(1, "blue"),
        YELLOW(2, "yellow"),
        GREEN(3, "green"),
        ORANGE(4, "orange");

        private final Integer index;
        private final String name;

        private EventColor(final Integer index, final String name) {
            this.index = index;
            this.name = name;
        }

        private static EventColor findByIndex(final Integer index) {
            for (final EventColor eventColor : EventColor.values()) {
                if (eventColor.index.equals(index)) {
                    return eventColor;
                }
            }

            throw new IllegalArgumentException("EventColor not found!");
        }

        public static String assignEventColor() {
            return EventColor.findByIndex(colorIndex.getAndIncrement() % EventColor.values().length).name;
        }
    }

    private static final long serialVersionUID = 8809846159698134690L;

    @JsonProperty(value = "id")
    private String id;

    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "start")
    private String start;

    @JsonProperty(value = "end")
    private String end;

    @JsonProperty(value = "color")
    private String color = EventColor.LIGHTGRAY.name();

    @JsonProperty(value = "resources")
    private List<String> resources;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(final String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(final String end) {
        this.end = end;
    }

    public String getColor() {
        return color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(final List<String> resources) {
        this.resources = resources;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EventDto)) {
            return false;
        }
        final EventDto other = (EventDto) obj;

        if (title == null) {
            if (other.title != null) {
                return false;
            }
        } else if (!title.equals(other.title)) {
            return false;
        }
        if (start == null) {
            if (other.start != null) {
                return false;
            }
        } else if (!start.equals(other.start)) {
            return false;
        }
        if (end == null) {
            if (other.end != null) {
                return false;
            }
        } else if (!end.equals(other.end)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
