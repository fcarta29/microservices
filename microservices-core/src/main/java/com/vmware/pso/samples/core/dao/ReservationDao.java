package com.vmware.pso.samples.core.dao;

import java.util.Set;
import java.util.UUID;

import com.vmware.pso.samples.core.model.Reservation;
import com.vmware.pso.samples.core.model.types.Status;

public interface ReservationDao extends Dao<Reservation> {

    public Set<Reservation> findByDateRange(final Long startTimestamp, final Long endTimestamp);

    public Set<Reservation> findByGroupAndStatus(final UUID groupId, final Status status);

}
