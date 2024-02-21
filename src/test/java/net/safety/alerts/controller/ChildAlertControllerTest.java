package net.safety.alerts.controller;


import net.safety.alerts.service.ChildAlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChildAlertController.class)
public class ChildAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChildAlertService childAlertService;

    @Test
    public void testChildAlertControllerReturns200() throws Exception {
        mockMvc.perform(get("/childAlert?address=1 st Test"))
                .andExpect(status().isOk());
    }
}
