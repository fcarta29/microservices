package com.vmware.pso.samples.core.model;

import java.util.UUID;

public class Server extends AbstractUUIDEntity {

    private static final long serialVersionUID = -7276263835137777339L;

    private String name;
    private boolean active;
    private UUID dataCenterId;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public UUID getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(final UUID dataCenterId) {
        this.dataCenterId = dataCenterId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((dataCenterId == null) ? 0 : dataCenterId.hashCode());
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
        final Server other = (Server) obj;
        if (name == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!name.equals(other.getName())) {
            return false;
        }
        if (dataCenterId == null) {
            if (other.getDataCenterId() != null) {
                return false;
            }
        } else if (!dataCenterId.equals(other.getDataCenterId())) {
            return false;
        }

        return true;
    }
}
