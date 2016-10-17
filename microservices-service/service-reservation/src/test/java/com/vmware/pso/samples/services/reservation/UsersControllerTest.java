package com.vmware.pso.samples.services.reservation;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.vmware.pso.samples.core.dao.UserDao;
import com.vmware.pso.samples.core.model.User;
import com.vmware.pso.samples.services.reservation.controller.UsersController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ReservationApplication.class })
@WebAppConfiguration
public class UsersControllerTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UsersController usersController;

    MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(usersController).build();
    }

    @Test
    public void testGetList() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUserName("Test User");
        List<User> users = new ArrayList<User>();
        users.add(user);
        when(userDao.list()).thenReturn(users);
        try {
            mockMvc.perform(get("/api/users")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(user.getId().toString())))
                    .andExpect(jsonPath("$[0].name", is(user.getUserName())));
        } catch (Exception e) {
            Assert.fail("testGetList failed with error:" + e.getLocalizedMessage());
        }

    }

}
