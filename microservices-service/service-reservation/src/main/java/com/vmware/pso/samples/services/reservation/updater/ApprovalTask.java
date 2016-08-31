package com.vmware.pso.samples.services.reservation.updater;

import java.util.UUID;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.model.Reservation;
import com.vmware.pso.samples.core.model.types.Status;

public class ApprovalTask implements Callable<Reservation> {

    @Autowired
    private ReservationDao reservationDao;

    private final Logger LOG = Logger.getLogger(ApprovalTask.class);

    private final UUID approvalId;
    private final UUID requestId;

    public ApprovalTask(final UUID requestId) {
        this.approvalId = UUID.randomUUID();
        this.requestId = requestId;
    }

    public UUID getApprovalId() {
        return approvalId;
    }

    @Override
    public Reservation call() {
        LOG.debug("Processing approval : " + approvalId);
        final Reservation reservation = reservationDao.get(requestId);
        LOG.debug("Found reservation: " + reservation);

        // TODO[fcarta] add better logic here for approval maybe?
        reservation.setStatus(Status.APPROVED);
        reservationDao.save(reservation);

        LOG.debug("Approved reservation: " + reservation);
        return reservation;
    }
}
