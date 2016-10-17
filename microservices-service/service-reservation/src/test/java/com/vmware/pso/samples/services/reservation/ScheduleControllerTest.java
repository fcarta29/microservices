package com.vmware.pso.samples.services.reservation;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
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

import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.dao.ServerDao;
import com.vmware.pso.samples.core.dao.UserDao;
import com.vmware.pso.samples.core.dto.EventDto.EventColor;
import com.vmware.pso.samples.core.model.Reservation;
import com.vmware.pso.samples.core.model.Server;
import com.vmware.pso.samples.core.model.User;
import com.vmware.pso.samples.core.model.types.Status;
import com.vmware.pso.samples.services.reservation.controller.ScheduleController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ReservationApplication.class })
@WebAppConfiguration
public class ScheduleControllerTest {

    @Mock
    private ReservationDao reservationDao;

    @Mock
    private ServerDao serverDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private ScheduleController scheduleController;

    private MockMvc mockMvc;
    private Server server;
    private User user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(scheduleController).build();
        server = new Server();
        server.setId(UUID.randomUUID());
        server.setName("Test Server");
        when(serverDao.get(any())).thenReturn(server);
        when(serverDao.findByName(any(), any())).thenReturn(server);

        user = new User();
        user.setUserName("Test User");
        when(userDao.get(any())).thenReturn(user);

    }

    @Test
    public void testGetSchedule() {
        Reservation reservation = createReservation();
        Set<Reservation> set = new HashSet<Reservation>();
        set.add(reservation);
        when(reservationDao.findByDateRange(any(), any())).thenReturn(set);

        try {
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            mockMvc.perform(get("/api/schedule").param("start", "0100").param("end", "0300"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.events[0].id", is(reservation.getId().toString())))
                    .andExpect(
                            jsonPath("$.events[0].title", is(reservation.getName() + System.lineSeparator() + "("
                                    + server.getName() + " - " + user.getUserName() + ")")))
                    .andExpect(jsonPath("$.events[0].resources[0]", is(server.getName())))
                    .andExpect(jsonPath("$.events[0].color", is(EventColor.assignEventColor(reservation.getId()))))
                    .andExpect(jsonPath("$.events[0].start", is(df.format(0100L))))
                    .andExpect(jsonPath("$.events[0].end", is(df.format(0300L))));
        } catch (Exception e) {
            Assert.fail("testGetList failed with error:" + e.getLocalizedMessage());
        }

    }

    private Reservation createReservation() {
        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setName("Test");
        reservation.setCreatedTimestamp(0200L);
        reservation.setDataCenterId(UUID.randomUUID());
        reservation.setEndTimestamp(0300L);
        reservation.setGroupId(UUID.randomUUID());
        reservation.setModifiedTimestamp(0400L);
        reservation.setServerId(UUID.randomUUID());
        reservation.setStartTimestamp(0100L);
        reservation.setStatus(Status.APPROVED);
        reservation.setUserId(UUID.randomUUID());
        return reservation;
    }
}
