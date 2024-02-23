package net.safety.alerts.controller;

import net.safety.alerts.service.FireStationService;
import net.safety.alerts.service.PhoneAlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PhoneAlertController.class)
public class PhoneAlertControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private PhoneAlertService phoneAlertService;
    @MockBean
    private FireStationService fireStationService;

    @Test
    public void testingPhoneAlertControllerReturns200() throws Exception {
        when(fireStationService.doesStationNumberExist("2")).thenReturn(true);
        mockMvc.perform(get("/phoneAlert?firestation=2"))
                .andExpect(status().isOk());
    }
    @Test
    public void testingPhoneAlertControllerReturns400() throws Exception {
        mockMvc.perform(get("/phoneAlert?firestation="))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testingPhoneAlertControllerReturns404() throws Exception {
        when(fireStationService.doesStationNumberExist("2")).thenReturn(false);
        mockMvc.perform(get("/phoneAlert?firestation=99"))
                .andExpect(status().isNotFound());
    }
}
