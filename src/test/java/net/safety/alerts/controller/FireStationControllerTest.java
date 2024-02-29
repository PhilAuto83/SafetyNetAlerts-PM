package net.safety.alerts.controller;


import net.safety.alerts.dto.PersonByFireStation;
import net.safety.alerts.dto.PersonDTO;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.service.FireStationService;
import net.safety.alerts.utils.AlertsUtility;
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


import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FireStationController.class)
public class FireStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FireStationService fireStationService;

    private PersonByFireStation personByFireStation;
    private static final FireStation VALID_STATION = new FireStation("11 Test ave", "23");
    private static final FireStation INVALID_STATION = new FireStation("11", "223");
    private static final FireStation EMPTY_STATION = new FireStation("", "");


    @BeforeEach
    public void setUpData(){
        personByFireStation = new PersonByFireStation(List.of(new PersonDTO("Phil", "Test","1 st Test", "444-555-8888")),1,0);
    }

    @Test
    public void givenStationNumberExists_whenCallingController_thenPersonByFireStationIsOk() throws Exception {
        when(fireStationService.doesStationNumberExist(any(String.class))).thenReturn(true);
        when(fireStationService.getPersonsInfoByStationNumber(any(String.class))).thenReturn(personByFireStation);
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
        when(fireStationService.doesStationNumberExist(any(String.class))).thenReturn(false);
        mockMvc.perform(get("/firestation?stationNumber=1"))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "Station number {0} should return bad request")
    @ValueSource(ints={0, -1})
    public void givenWrongStationNumber_whenCallingController_thenReturn400(int stationNumber) throws Exception {
        when(fireStationService.doesStationNumberExist(any(String.class))).thenReturn(false);
        mockMvc.perform(get("/firestation?stationNumber="+stationNumber))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createValidFireStationReturns200() throws Exception {
        when(fireStationService.doesStationAlreadyExist(VALID_STATION)).thenReturn(false);
        when(fireStationService.save(VALID_STATION)).thenReturn(VALID_STATION);
        mockMvc.perform(post("/firestation")
                        .content(AlertsUtility.convertObjectToString(VALID_STATION))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.address", is("11 Test ave")))
                .andExpect(jsonPath("$.station", is("23")));
    }
    @Test
    public void whenPostingExistingStationReturns204() throws Exception {
        when(fireStationService.doesStationAlreadyExist(VALID_STATION)).thenReturn(true);
        mockMvc.perform(post("/firestation")
                        .content(AlertsUtility.convertObjectToString(VALID_STATION))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));
    }

    @Test
    public void whenPostingInvalidFormatStationReturns400() throws Exception {
        mockMvc.perform(post("/firestation")
                        .content(AlertsUtility.convertObjectToString(INVALID_STATION))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("must start with a digit and can contain only spaces, '.', digits or letters. Length must be minimum 5 characters.")))
                .andExpect(jsonPath("$.message", containsString("station number must be positive with maximum 2 digits whose minimum value starts at 1")));
    }
    @Test
    public void whenPostingEmptyStationReturns400() throws Exception {
        mockMvc.perform(post("/firestation")
                        .content(AlertsUtility.convertObjectToString(EMPTY_STATION))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("must start with a digit and can contain only spaces, '.', digits or letters. Length must be minimum 5 characters.")))
                .andExpect(jsonPath("$.message", containsString("station number must be positive with maximum 2 digits whose minimum value starts at 1")));
    }
}
