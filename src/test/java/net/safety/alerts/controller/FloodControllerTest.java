package net.safety.alerts.controller;


import net.safety.alerts.dto.FloodDTO;
import net.safety.alerts.dto.PersonMedicalDataDTO;
import net.safety.alerts.service.FireStationService;
import net.safety.alerts.service.FloodService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FloodController.class)
public class FloodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FloodService floodService;

    @MockBean
    private FireStationService fireStationService;

    private static List<FloodDTO> floodData = new ArrayList<>();
    private static Map<String, List<String>> medicalData = new HashMap<>();

    @BeforeAll
    public static void setUp(){
        medicalData.put("medications", List.of("doliprane:500mg"));
        medicalData.put("allergies", List.of("peanut","oil"));
        floodData.add(new FloodDTO("1 st Test", List.of(new PersonMedicalDataDTO("Phil Test",
                "333-555-1111",26,medicalData))));
    }

    @Test
    public void testFireControllerReturns200() throws Exception {
        when(fireStationService.doesStationNumberExist("4")).thenReturn(true);
        when(floodService.getPersonsWithMedicalDataFromStationNumber("4")).thenReturn(floodData);
        mockMvc.perform(get("/flood/stations?stations=4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].address", is("1 st Test")))
                .andExpect(jsonPath("$.[0].persons[0].age", is(26)))
                .andExpect(jsonPath("$.[0].persons[0].medicalInfos.medications[0]", is("doliprane:500mg")))
                .andExpect(jsonPath("$.[0].persons[0].medicalInfos.allergies[1]", is("oil")));
    }

    @Test
    public void testStationNumberDoesNotExistReturns404() throws Exception {
        when(fireStationService.doesStationNumberExist("4")).thenReturn(false);
        mockMvc.perform(get("/flood/stations?stations=4"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEmptyStationNumberReturns400() throws Exception {
        mockMvc.perform(get("/flood/stations?stations="))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("must be a positive number with maximum 2 digits whose minimum value starts at 1")));
    }


}
