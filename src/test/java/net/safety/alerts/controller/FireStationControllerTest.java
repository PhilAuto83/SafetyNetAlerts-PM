package net.safety.alerts.controller;

import net.safety.alerts.dto.PersonByFireStation;
import net.safety.alerts.dto.PersonDTO;
import net.safety.alerts.service.FireStationService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;


import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FireStationController.class)
public class FireStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FireStationService fireStationService;

    private PersonByFireStation personByFireStation;

    @BeforeEach
    public void setUpData(){
        personByFireStation = new PersonByFireStation(List.of(new PersonDTO("Phil", "Test","1 st Test", "444-555-8888")),1,0);


    }

    @Test
    public void givenStationNumberExists_whenCallingController_thenPersonByFireStationIsOk() throws Exception {
        when(fireStationService.doesStationNumberExist(any(Integer.class))).thenReturn(true);
        when(fireStationService.getPersonsInfoByStationNumber(any(Integer.class))).thenReturn(personByFireStation);
        mockMvc.perform(get("/firestation?stationNumber=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persons",hasSize(1)))
                .andExpect(jsonPath("$.persons[0].firstName", Matchers.is("Phil")))
                .andExpect(jsonPath("$.persons[0].lastName", Matchers.is("Test")))
                .andExpect(jsonPath("$.persons[0].phone", Matchers.is("444-555-8888")))
                .andExpect(jsonPath("$.nbAdults", Matchers.is(1)))
                .andExpect(jsonPath("$.nbChildren", Matchers.is(0)));
    }

    @Test
    public void givenStationNumberDoNotExist_whenCallingController_thenReturnNotFound() throws Exception {
        when(fireStationService.doesStationNumberExist(any(Integer.class))).thenReturn(false);
        mockMvc.perform(get("/firestation?stationNumber=1"))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "Station number {0} should return bad request")
    @ValueSource(ints={0, -1})
    public void givenWrongStationNumber_whenCallingController_thenReturn400(int stationNumber) throws Exception {
        when(fireStationService.doesStationNumberExist(any(Integer.class))).thenReturn(false);
        mockMvc.perform(get("/firestation?stationNumber="+stationNumber))
                .andExpect(status().isBadRequest());
    }
}
