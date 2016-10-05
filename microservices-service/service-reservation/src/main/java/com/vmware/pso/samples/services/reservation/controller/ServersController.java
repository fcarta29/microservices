package com.vmware.pso.samples.services.reservation.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.ServerDao;
import com.vmware.pso.samples.core.dto.ServerDto;
import com.vmware.pso.samples.core.model.Server;

@RestController
@RequestMapping("/api/servers")
public class ServersController extends AbstractReservationController<ServerDto, Server> {

    @Autowired
    private ServerDao serverDao;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody Collection<ServerDto> getList() {
        return toDtoList(serverDao.list());
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    final public @ResponseBody ServerDto create(@RequestBody final ServerDto serverDto) {
        // TODO[fcarta] data validation here
        final Server server = toEntity(serverDto);
        serverDao.save(server);

        return serverDto;
    }

    @Override
    protected ServerDto toDto(final Server server) {
        final ServerDto serverDto = new ServerDto();
        serverDto.setId(server.getId().toString());
        serverDto.setName(server.getName());
        return serverDto;
    }

    @Override
    protected Server toEntity(final ServerDto serverDto) {
        final Server server = new Server();
        // TODO[fcarta] need to fix these when ready for full impl
        server.setDataCenterId(DEFAULT_DATACENTER_ID);
        server.setActive(Boolean.TRUE);
        server.setName(serverDto.getName());
        return server;
    }
}
