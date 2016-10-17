package com.vmware.pso.samples.services.reservation;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.vmware.pso.samples.core.dao.ServerDao;
import com.vmware.pso.samples.core.dto.ServerDto;
import com.vmware.pso.samples.core.model.Server;
import com.vmware.pso.samples.services.reservation.controller.ServersController;
import com.vmware.pso.samples.services.util.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ReservationApplication.class})
@WebAppConfiguration
public class ServersControllerTest {

	@Mock
    private ServerDao serverDao;
	
    @InjectMocks
    private ServersController serversController;
    

    MockMvc mockMvc;
    @Before
    public void setUp() {
    	 MockitoAnnotations.initMocks(this);  	 
         mockMvc = MockMvcBuilders.standaloneSetup(serversController).build();
    }

    @Test
    public void testGetList() {
    	Server server = createServer();
        List<Server> list = new ArrayList<Server>();
        list.add(server);
      
        when(serverDao.list()).thenReturn(list);        
        
        try {
			mockMvc.perform(get("/api/servers"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].id", is(server.getId().toString())))
			.andExpect(jsonPath("$[0].name", is(server.getName())));
		} catch (Exception e) {
			Assert.fail("testGetList failed with error:"+e.getLocalizedMessage());
		}

    }


    @Test
    public void testCreate() throws Exception {
    	Server server = createServer();
        Mockito.doAnswer(new Answer<Object>(){
            @Override
            public Object answer(InvocationOnMock invocation){
               Object[] arguments = invocation.getArguments();
               Server argument = (Server) arguments[0];
               argument.setId(UUID.randomUUID());
               return null;
            }
         }).when(serverDao).save(any());
        
        ServerDto serverDto = new ServerDto();
        serverDto.setName("Test Server");
        serverDto.setId(server.getId().toString());
        
        mockMvc.perform(post("/api/servers").contentType("application/json").content(TestUtil.convertObjectToJsonBytes(serverDto)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.name", is(serverDto.getName())))
		.andExpect(jsonPath("$.id", is(server.getId().toString())));
    }
  
    private Server createServer(){
    	Server server = new Server();
        server.setId(UUID.randomUUID());
        server.setName("Test Server");
        return server;
    }
}
