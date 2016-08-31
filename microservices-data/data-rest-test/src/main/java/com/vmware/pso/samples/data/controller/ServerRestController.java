package com.vmware.pso.samples.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Override
    protected void merge(final Server persistedServer, final Server server) {
        persistedServer.setName(server.getName());
        persistedServer.setDataCenterId(server.getDataCenterId());
        persistedServer.setActive(server.isActive());
    }
}
