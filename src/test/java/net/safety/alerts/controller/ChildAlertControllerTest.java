package net.safety.alerts.controller;


import net.safety.alerts.dto.ChildAlertDTO;
import net.safety.alerts.dto.PersonAgeDTO;
import net.safety.alerts.service.ChildAlertService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChildAlertController.class)
public class ChildAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChildAlertService childAlertService;

    private static ChildAlertDTO childAlertDTO;

    @BeforeAll
    public static void setUp(){
        childAlertDTO = new ChildAlertDTO(List.of(new PersonAgeDTO("Phil", "Child",2)), List.of(new PersonAgeDTO("Phil","Adult",41)));
    }

    @Test
    public void testChildAlertControllerReturns200() throws Exception {
        when(childAlertService.getAddressOccurrence("1 st Test")).thenReturn(1);
        when(childAlertService.getPersonFromAddress("1 st Test")).thenReturn(childAlertDTO);
        mockMvc.perform(get("/childAlert?address=1 st Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.childrenList", hasSize(1)))
                .andExpect(jsonPath("$.otherMembers", hasSize(1)));
    }

    @Test
    public void testChildAlertControllerReturns404() throws Exception {
        when(childAlertService.getAddressOccurrence("1 st Test")).thenReturn(0);
        mockMvc.perform(get("/childAlert?address=1 st Test"))
                .andExpect(status().isNotFound());
    }
}
