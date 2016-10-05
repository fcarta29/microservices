package com.vmware.pso.samples.core.dao;

import java.util.UUID;

import com.vmware.pso.samples.core.model.User;

public interface UserDao extends Dao<User> {

    public User findByUserName(final UUID groupId, final String username);
}
