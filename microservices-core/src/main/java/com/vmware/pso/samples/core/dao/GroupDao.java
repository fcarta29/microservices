package com.vmware.pso.samples.core.dao;

import com.vmware.pso.samples.core.model.Group;

public interface GroupDao extends Dao<Group> {

    public Group findByName(final String name);

}
