package com.vmware.pso.samples.core.model;

import java.util.UUID;

import com.vmware.pso.samples.core.model.types.Status;

public class Reservation extends AbstractUUIDEntity {

    private static final long serialVersionUID = 331152675278900679L;

    private String name;
    private UUID dataCenterId;
    private UUID serverId;
    private UUID groupId;
    private UUID userId;
    private Long startTimestamp;
    private Long endTimestamp;
    private Long createdTimestamp;
    private Long modifiedTimestamp;
    private Status status;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public UUID getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(final UUID dataCenterId) {
        this.dataCenterId = dataCenterId;
    }

    public UUID getServerId() {
        return serverId;
    }

    public void setServerId(final UUID serverId) {
        this.serverId = serverId;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(final UUID groupId) {
        this.groupId = groupId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(final UUID userId) {
        this.userId = userId;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(final Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(final Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(final Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Long getModifiedTimestamp() {
        return modifiedTimestamp;
    }

    public void setModifiedTimestamp(final Long modifiedTimestamp) {
        this.modifiedTimestamp = modifiedTimestamp;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((dataCenterId == null) ? 0 : dataCenterId.hashCode());
        result = prime * result + ((serverId == null) ? 0 : serverId.hashCode());
        result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result + ((startTimestamp == null) ? 0 : startTimestamp.hashCode());
        result = prime * result + ((endTimestamp == null) ? 0 : endTimestamp.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Reservation other = (Reservation) obj;
        if (dataCenterId == null) {
            if (other.getDataCenterId() != null) {
                return false;
            }
        } else if (!dataCenterId.equals(other.getDataCenterId())) {
            return false;
        }
        if (serverId == null) {
            if (other.getServerId() != null) {
                return false;
            }
        } else if (!serverId.equals(other.getServerId())) {
            return false;
        }
        if (groupId == null) {
            if (other.getGroupId() != null) {
                return false;
            }
        } else if (!groupId.equals(other.getGroupId())) {
            return false;
        }
        if (userId == null) {
            if (other.getUserId() != null) {
                return false;
            }
        } else if (!userId.equals(other.getUserId())) {
            return false;
        }
        if (startTimestamp == null) {
            if (other.getStartTimestamp() != null) {
                return false;
            }
        } else if (!startTimestamp.equals(other.getStartTimestamp())) {
            return false;
        }
        if (endTimestamp == null) {
            if (other.getEndTimestamp() != null) {
                return false;
            }
        } else if (!endTimestamp.equals(other.getEndTimestamp())) {
            return false;
        }
        return true;
    }

}
