package com.vmware.pso.samples.services.approval.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.GroupDao;
import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.dto.ApprovalDto;
import com.vmware.pso.samples.core.model.Group;
import com.vmware.pso.samples.core.model.Reservation;
import com.vmware.pso.samples.core.model.types.Status;

@RestController
@RequestMapping("/api/v1/approvables")
public class ApprovablesController extends AbstractApprovalController<ApprovalDto, Reservation> {

    private static AtomicInteger fakeId = new AtomicInteger(0);

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private GroupDao groupDao;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody Collection<ApprovalDto> getList(
            @RequestParam(required = false, defaultValue = "true") final boolean approved,
            @RequestParam(required = false, defaultValue = "Team2") final String teamID) {

        final Group group = groupDao.findByName(teamID);
        if (group == null) {
            return Collections.emptyList();
        }
        // TODO[fcarta] not including any rejected reservations because approvals are binary for testing
        final Status status = (approved) ? Status.APPROVED : Status.WAITING;
        return toDtoList(reservationDao.findByGroupAndStatus(group.getId(), status));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody ApprovalDto get(@PathVariable("id") final UUID id) {
        return toDto(reservationDao.get(id));
    }

    @Override
    protected ApprovalDto toDto(final Reservation reservation) {
        final ApprovalDto approvalDto = new ApprovalDto();
        // TODO[fcarta] The SoapUI tests script test assumes this is a number so UUIDs will not work - for now faking it
        // approvalDto.setId(reservation.getId().toString());
        approvalDto.setId(fakeId.incrementAndGet());
        final Group group = groupDao.get(reservation.getGroupId());
        approvalDto.setTeamId(group.getName());
        approvalDto.setReservationId(reservation.getId().toString());
        approvalDto.setApproved(reservation.getStatus().isApproved());
        return approvalDto;
    }

    @Override
    protected Reservation toEntity(final ApprovalDto approvalDto) {
        throw new UnsupportedOperationException("Approvals toEntity() not implemented yet!");
    }
}
