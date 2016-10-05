package com.vmware.pso.samples.data.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.Dao;
import com.vmware.pso.samples.core.dao.ServerDao;
import com.vmware.pso.samples.core.model.Server;

@RestController
@RequestMapping("/servers")
public class ServerRestController extends AbstractRestController<Server> {

    @Autowired
    private ServerDao serverDao;

    @Override
    protected Dao<Server> getDao() {
        return serverDao;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody Collection<Server> searchServers(@RequestParam(required = false) final UUID dataCenterId,
            @RequestParam(required = false) final String name) {
        final Collection<Server> servers = new ArrayList<Server>();

        if (dataCenterId != null && StringUtils.isNotBlank(name)) {
            final Server foundServer = serverDao.findByName(dataCenterId, name);
            if (foundServer != null) {
                servers.add(foundServer);
            }
        }

        return servers;
    }

    @Override
    protected void merge(final Server persistedServer, final Server server) {
        persistedServer.setName(server.getName());
        persistedServer.setDataCenterId(server.getDataCenterId());
        persistedServer.setActive(server.isActive());
    }
}
