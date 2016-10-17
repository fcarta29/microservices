package com.vmware.pso.samples.services.reservation;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.vmware.pso.samples.core.dao.GroupDao;
import com.vmware.pso.samples.core.dao.ReservationDao;
import com.vmware.pso.samples.core.dao.ServerDao;
import com.vmware.pso.samples.core.dao.UserDao;
import com.vmware.pso.samples.core.dto.ReservationDto;
import com.vmware.pso.samples.core.dto.TopicDto;
import com.vmware.pso.samples.core.model.Group;
import com.vmware.pso.samples.core.model.Reservation;
import com.vmware.pso.samples.core.model.Server;
import com.vmware.pso.samples.core.model.User;
import com.vmware.pso.samples.core.model.types.Status;
import com.vmware.pso.samples.services.reservation.controller.ReservationsController;
import com.vmware.pso.samples.services.reservation.updater.ApprovalScheduledExecutor;
import com.vmware.pso.samples.services.util.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ReservationApplication.class })
@WebAppConfiguration
public class ReservationControllerTest {

    @Mock
    private ReservationDao reservationDao;

    @Mock
    private ServerDao serverDao;

    @Mock
    private UserDao userDao;

    @Mock
    private GroupDao groupDao;

    @Mock
    private ApprovalScheduledExecutor approvalScheduledExecutor;

    @Mock
    private RedisTemplate<String, ReservationDto> redisTemplate;

    @Mock
    private RedisTemplate<String, TopicDto> topicRedisTemplate;

    @InjectMocks
    private ReservationsController reservationsController;

    MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(reservationsController).build();
        Server server = new Server();
        server.setId(UUID.randomUUID());
        server.setName("Test Server");
        when(serverDao.get(any())).thenReturn(server);
        when(serverDao.findByName(any(), any())).thenReturn(server);

        User user = new User();
        user.setUserName("Test User");
        when(userDao.get(any())).thenReturn(user);

        Group group = new Group();
        group.setName("Test Group");
        when(groupDao.get(any())).thenReturn(group);
    }

    @Test
    public void testGetList() {
        List<Reservation> list = new ArrayList<Reservation>();
        list.add(createReservation());

        when(reservationDao.list()).thenReturn(list);

        try {
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            mockMvc.perform(get("/api/reservations")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name", is("Test"))).andExpect(jsonPath("$[0].approved", is(true)))
                    .andExpect(jsonPath("$[0].owner_name", is("Test User")))
                    .andExpect(jsonPath("$[0].server_name", is("Test Server")))
                    .andExpect(jsonPath("$[0].start_date", is(df.format(0100L))))
                    .andExpect(jsonPath("$[0].end_date", is(df.format(0300L))));
        } catch (Exception e) {
            Assert.fail("testGetList failed with error:" + e.getLocalizedMessage());
        }

    }

    @Test
    public void testGetById() {
        Reservation reservation = createReservation();
        when(reservationDao.get(any())).thenReturn(reservation);

        try {
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            mockMvc.perform(get("/api/reservations/{id}", reservation.getId())).andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Test"))).andExpect(jsonPath("$.approved", is(true)))
                    .andExpect(jsonPath("$.owner_name", is("Test User")))
                    .andExpect(jsonPath("$.server_name", is("Test Server")))
                    .andExpect(jsonPath("$.start_date", is(df.format(0100L))))
                    .andExpect(jsonPath("$.end_date", is(df.format(0300L))));
        } catch (Exception e) {
            Assert.fail("testGetList failed with error:" + e.getLocalizedMessage());
        }

    }

    @Test
    public void testCreate() throws Exception {
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                Object[] arguments = invocation.getArguments();
                Reservation argument = (Reservation) arguments[0];
                argument.setId(UUID.randomUUID());
                return null;
            }
        }).when(reservationDao).save(any());

        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        Date startdate = formatter.parse("10/10/16");
        Date enddate = formatter.parse("10/10/16");

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setName("Test");
        reservationDto.setServerName("Test Server");
        reservationDto.setStartDate(df.format(startdate));
        reservationDto.setEndDate(df.format(enddate));

        mockMvc.perform(
                post("/api/reservations").contentType("application/json").content(
                        TestUtil.convertObjectToJsonBytes(reservationDto))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test"))).andExpect(jsonPath("$.approved", is(false)))
                .andExpect(jsonPath("$.owner_name", is("Test User")))
                .andExpect(jsonPath("$.server_name", is("Test Server")))
                .andExpect(jsonPath("$.start_date", is(df.format(startdate) + "T00:00:00.000Z")))
                .andExpect(jsonPath("$.end_date", is(df.format(enddate) + "T00:00:00.000Z")));
    }

    @Test
    public void testUpdate() throws Exception {
        Reservation reservation = createReservation();

        when(reservationDao.get(any())).thenReturn(reservation);

        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        Date startdate = formatter.parse("10/10/16");
        Date enddate = formatter.parse("10/10/16");

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setName("Test");
        reservationDto.setServerName("Test Server");
        reservationDto.setStartDate(df.format(startdate));
        reservationDto.setEndDate(df.format(enddate));
        reservationDto.setApproved(true);

        mockMvc.perform(
                put("/api/reservations/{id}", reservation.getId()).contentType("application/json").content(
                        TestUtil.convertObjectToJsonBytes(reservationDto))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test"))).andExpect(jsonPath("$.approved", is(true)))
                .andExpect(jsonPath("$.owner_name", is("Test User")))
                .andExpect(jsonPath("$.server_name", is("Test Server")))
                .andExpect(jsonPath("$.start_date", is(df.format(startdate) + "T00:00:00.000Z")))
                .andExpect(jsonPath("$.end_date", is(df.format(enddate) + "T00:00:00.000Z")));
    }

    @Test
    public void testDelete() throws Exception {
        Reservation reservation = createReservation();
        when(reservationDao.get(any())).thenReturn(reservation);
        mockMvc.perform(delete("/api/reservations/{id}", reservation.getId())).andExpect(status().isOk());
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
