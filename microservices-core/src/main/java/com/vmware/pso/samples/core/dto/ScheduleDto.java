package com.vmware.pso.samples.core.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.pso.samples.core.dto.types.TimeRange;

public class ScheduleDto extends AbstractDto {

    private static final long serialVersionUID = -4065297389931438015L;

    // by default return month
    @JsonProperty(value = "timeRange")
    private final TimeRange timeRange = TimeRange.MONTH;

    @JsonProperty(value = "events")
    private final List<EventDto> events = new ArrayList<EventDto>();

    @JsonProperty(value = "start")
    private String start;

    @JsonProperty(value = "end")
    private String end;

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

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public List<EventDto> getEvents() {
        return events;
    }

    public void addEvent(final EventDto eventDto) {
        getEvents().add(eventDto);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        result = prime * result + ((timeRange == null) ? 0 : timeRange.hashCode());
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
        if (!(obj instanceof ScheduleDto)) {
            return false;
        }
        final ScheduleDto other = (ScheduleDto) obj;
        if (end == null) {
            if (other.end != null) {
                return false;
            }
        } else if (!end.equals(other.end)) {
            return false;
        }
        if (start == null) {
            if (other.start != null) {
                return false;
            }
        } else if (!start.equals(other.start)) {
            return false;
        }
        if (timeRange != other.timeRange) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
