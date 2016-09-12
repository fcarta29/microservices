package com.vmware.pso.samples.core.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApprovalDto extends AbstractDto {

    private static final long serialVersionUID = -2966621534126358794L;

    @JsonProperty(value = "id")
    private Number id;

    @JsonProperty(value = "teamID")
    private String teamId;

    @JsonProperty(value = "approved")
    private boolean approved;

    public Number getId() {
        return id;
    }

    public void setId(final Number id) {
        this.id = id;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(final String teamId) {
        this.teamId = teamId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(final boolean approved) {
        this.approved = approved;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((teamId == null) ? 0 : teamId.hashCode());
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
        if (!(obj instanceof ApprovalDto)) {
            return false;
        }
        final ApprovalDto other = (ApprovalDto) obj;
        if (id == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!id.equals(other.getId())) {
            return false;
        }
        if (teamId == null) {
            if (other.getTeamId() != null) {
                return false;
            }
        } else if (!teamId.equals(other.getTeamId())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
