package com.vmware.pso.samples.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.Dao;
import com.vmware.pso.samples.core.dao.GroupDao;
import com.vmware.pso.samples.core.model.Group;

@RestController
@RequestMapping("/groups")
public class GroupRestController extends AbstractRestController<Group> {

    @Autowired
    private GroupDao groupDao;

    @Override
    protected Dao<Group> getDao() {
        return groupDao;
    }

    @Override
    protected void merge(final Group persistedGroup, final Group group) {
        persistedGroup.setName(group.getName());
        persistedGroup.setActive(group.isActive());
    }

}
