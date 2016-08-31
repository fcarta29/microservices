package com.vmware.pso.samples.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.Dao;
import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.model.Reservation;
import com.vmware.pso.samples.core.model.types.Status;

@RestController
@RequestMapping("/reservations")
public class ReservationRestController extends AbstractRestController<Reservation> {

    @Autowired
    private ReservationDao reservationDao;

    @Override
    protected Dao<Reservation> getDao() {
        return reservationDao;
    }

    @Override
    public void createEntity(final Reservation reservation) {
        reservation.setCreatedTimestamp(System.currentTimeMillis());
        reservation.setModifiedTimestamp(System.currentTimeMillis());
        reservation.setStatus(Status.WAITING);
        getDao().save(reservation);
    }

    @Override
    public void updateEntity(final Reservation persistedReservation, final Reservation reservation) {
        merge(persistedReservation, reservation);
        persistedReservation.setModifiedTimestamp(System.currentTimeMillis());
        getDao().save(persistedReservation);
    }

    @Override
    protected void merge(final Reservation persistedReservation, final Reservation reservation) {
        persistedReservation.setName(reservation.getName());
        persistedReservation.setDataCenterId(reservation.getDataCenterId());
        persistedReservation.setServerId(reservation.getServerId());
        persistedReservation.setGroupId(reservation.getGroupId());
        persistedReservation.setUserId(reservation.getUserId());
        persistedReservation.setStartTimestamp(reservation.getStartTimestamp());
        persistedReservation.setEndTimestamp(reservation.getEndTimestamp());
        // persistedReservation.setCreatedTimestamp(reservation.getCreatedTimestamp());
        // persistedReservation.setModifiedTimestamp(reservation.getModifiedTimestamp());
        persistedReservation.setStatus(reservation.getStatus());
    }
}
