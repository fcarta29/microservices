package com.vmware.pso.samples.core.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReservationDto extends AbstractDto {

    private static final long serialVersionUID = -8151606863232844631L;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "server_name")
    private String serverName;

    @JsonProperty(value = "start_date")
    private String startDate;

    @JsonProperty(value = "end_date")
    private String endDate;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(final String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(final String endDate) {
        this.endDate = endDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((serverName == null) ? 0 : serverName.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
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
        if (!(obj instanceof ReservationDto)) {
            return false;
        }
        final ReservationDto other = (ReservationDto) obj;
        if (name == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!name.equals(other.getName())) {
            return false;
        }
        if (serverName == null) {
            if (other.getServerName() != null) {
                return false;
            }
        } else if (!serverName.equals(other.getServerName())) {
            return false;
        }
        if (startDate == null) {
            if (other.getStartDate() != null) {
                return false;
            }
        } else if (!startDate.equals(other.getStartDate())) {
            return false;
        }
        if (endDate == null) {
            if (other.getEndDate() != null) {
                return false;
            }
        } else if (!endDate.equals(other.getEndDate())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
