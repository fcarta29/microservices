package com.vmware.pso.samples.core.dao;

import java.util.UUID;

import com.vmware.pso.samples.core.model.Server;

public interface ServerDao extends Dao<Server> {

    public Server findByName(final UUID dataCenterId, final String name);
}
