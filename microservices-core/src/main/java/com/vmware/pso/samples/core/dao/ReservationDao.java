package com.vmware.pso.samples.core.dao;

import java.util.Set;

import com.vmware.pso.samples.core.model.Reservation;

public interface ReservationDao extends Dao<Reservation> {

    public Set<Reservation> findByDateRange(final Long startTimestamp, final Long endTimestamp);

}
