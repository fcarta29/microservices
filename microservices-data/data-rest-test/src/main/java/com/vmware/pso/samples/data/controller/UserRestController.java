package com.vmware.pso.samples.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.Dao;
import com.vmware.pso.samples.core.dao.UserDao;
import com.vmware.pso.samples.core.model.User;

@RestController
@RequestMapping("/users")
public class UserRestController extends AbstractRestController<User> {

    @Autowired
    private UserDao userDao;

    @Override
    protected Dao<User> getDao() {
        return userDao;
    }

    @Override
    protected void merge(final User persistedUser, final User user) {
        persistedUser.setFirstName(user.getFirstName());
        persistedUser.setLastName(user.getLastName());
        persistedUser.setUserName(user.getUserName());
        persistedUser.setGroupId(user.getGroupId());
        persistedUser.setActive(user.isActive());
    }
}
