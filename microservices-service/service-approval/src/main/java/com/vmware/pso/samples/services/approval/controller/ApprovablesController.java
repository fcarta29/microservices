package com.vmware.pso.samples.services.approval.controller;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.dto.ApprovalDto;
import com.vmware.pso.samples.core.model.Reservation;

@RestController
@RequestMapping("/api/v1/approvables")
public class ApprovablesController extends AbstractApprovalController<ApprovalDto, Reservation> {

    private static AtomicInteger fakeId = new AtomicInteger(0);

    @Autowired
    private ReservationDao reservationDao;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody Collection<ApprovalDto> getList() {
        return toDtoList(reservationDao.list());
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
        approvalDto.setId(String.valueOf(fakeId.incrementAndGet()));
        System.out.println("The fake id: " + String.valueOf(fakeId.get()));
        approvalDto.setTeamId(reservation.getGroupId().toString());
        approvalDto.setApproved(reservation.getStatus().isApproved());
        return approvalDto;
    }

    @Override
    protected Reservation toEntity(final ApprovalDto approvalDto) {
        throw new UnsupportedOperationException("Approvals toEntity() not implemented yet!");
    }
}
