package com.vmware.pso.samples.services.reservation.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dto.UpdaterDto;
import com.vmware.pso.samples.services.reservation.updater.ApprovalScheduledExecutor;

@RestController
@RequestMapping("/api/updaters")
public class UpdatersController {

    @Autowired
    private ApprovalScheduledExecutor approvalScheduledExecutor;

    private static final String DEFAULT_UPDATER_ID = "1";

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody UpdaterDto get(@PathVariable("id") final String id) {
        return getUpdaterDto();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
    final public void save(@PathVariable("id") final String id, final @RequestBody UpdaterDto updatorDto) {

        if (!StringUtils.equals(id, DEFAULT_UPDATER_ID)) {
            throw new IllegalArgumentException("Updater does not exist");
        }

        if (updatorDto.isActive()) {
            approvalScheduledExecutor.resume();
        } else {
            approvalScheduledExecutor.pause();
        }
    }

    private UpdaterDto getUpdaterDto() {
        final UpdaterDto updaterDto = new UpdaterDto();
        updaterDto.setId(DEFAULT_UPDATER_ID);
        updaterDto.setActive(!approvalScheduledExecutor.isPaused());
        return updaterDto;
    }
}
