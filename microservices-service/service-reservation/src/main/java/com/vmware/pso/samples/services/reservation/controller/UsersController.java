package com.vmware.pso.samples.services.reservation.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.pso.samples.core.dao.UserDao;
import com.vmware.pso.samples.core.dto.UserDto;
import com.vmware.pso.samples.core.model.User;

@RestController
@RequestMapping("/api/users")
public class UsersController extends AbstractReservationController<UserDto, User> {

    @Autowired
    private UserDao userDao;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    final public @ResponseBody Collection<UserDto> getList() {
        return toDtoList(userDao.list());
    }

    @Override
    protected UserDto toDto(final User user) {
        final UserDto userDto = new UserDto();
        userDto.setId(user.getId().toString());
        userDto.setName(user.getUserName());
        return userDto;
    }

    @Override
    protected User toEntity(final UserDto userDto) {
        final User user = new User();
        // TODO[fcarta] need to fix these when ready for full impl
        user.setGroupId(DEFAULT_GROUP_ID);
        user.setActive(Boolean.TRUE);
        user.setUserName(userDto.getName());
        return user;
    }
}
