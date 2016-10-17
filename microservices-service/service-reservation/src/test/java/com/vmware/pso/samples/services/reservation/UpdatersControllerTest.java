package com.vmware.pso.samples.services.reservation;


import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.vmware.pso.samples.core.dto.UpdaterDto;
import com.vmware.pso.samples.services.reservation.controller.UpdatersController;
import com.vmware.pso.samples.services.reservation.updater.ApprovalScheduledExecutor;
import com.vmware.pso.samples.services.util.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={ReservationApplication.class})
@WebAppConfiguration
public class UpdatersControllerTest {

	@Mock
	private ApprovalScheduledExecutor approvalScheduledExecutor;
	
    @InjectMocks
    private UpdatersController updatersController;
    

    MockMvc mockMvc;
    @Before
    public void setUp() {
    	 MockitoAnnotations.initMocks(this);  	 
         mockMvc = MockMvcBuilders.standaloneSetup(updatersController).build();
    }

    @Test
    public void testGetList() {     
        try {
			mockMvc.perform(get("/api/updaters/{id}",1))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", is("1")))
			.andExpect(jsonPath("$.active", is(true)));
		} catch (Exception e) {
			Assert.fail("testGetList failed with error:"+e.getLocalizedMessage());
		}

    }

    @Test
    public void testCreate() throws Exception {        
        UpdaterDto updaterDto = new UpdaterDto();
        updaterDto.setActive(true);
        
        mockMvc.perform(put("/api/updaters/{id}","1").contentType("application/json").content(TestUtil.convertObjectToJsonBytes(updaterDto)))
		.andExpect(status().isOk());
    }
  
}
