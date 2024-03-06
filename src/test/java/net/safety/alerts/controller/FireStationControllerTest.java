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
import java.util.Objects;


import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        when(fireStationService.doesStationAlreadyExist(VALID_STATION.getAddress(), VALID_STATION.getStation())).thenReturn(false);
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
        when(fireStationService.doesStationAlreadyExist(VALID_STATION.getAddress(), VALID_STATION.getStation())).thenReturn(true);
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
                .andExpect(jsonPath("$.message", containsString("number must be positive with maximum 2 digits whose minimum value starts at 1")));
    }
    @Test
    public void whenPostingEmptyStationReturns400() throws Exception {
        mockMvc.perform(post("/firestation")
                        .content(AlertsUtility.convertObjectToString(EMPTY_STATION))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("must start with a digit and can contain only spaces, '.', digits or letters. Length must be minimum 5 characters.")))
                .andExpect(jsonPath("$.message", containsString("number must be positive with maximum 2 digits whose minimum value starts at 1")));
    }

    @Test
    public void whenInValidStationReturns400() throws Exception {
        FireStation stationWithSpaces = new FireStation(" ", " ");
        mockMvc.perform(post("/firestation")
                        .content(AlertsUtility.convertObjectToString(stationWithSpaces))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("must start with a digit and can contain only spaces, '.', digits or letters. Length must be minimum 5 characters.")))
                .andExpect(jsonPath("$.message", containsString("number must be positive with maximum 2 digits whose minimum value starts at 1")));
    }

    @Test
    public void whenPassingNullFireStationObjectReturns400() throws Exception {
        mockMvc.perform(post("/firestation")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("address: cannot be null")))
                .andExpect(jsonPath("$.message", containsString("station: cannot be null")));
    }

    @Test
    public void whenDeletingValidAddressReturns200() throws Exception {
        when(fireStationService.doesNumberOrAddressExists("2th Elvis Blvd")).thenReturn(true);
        mockMvc.perform(delete("/firestation/2th Elvis Blvd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("List of stations with number or address 2th Elvis Blvd have been removed successfully")));
    }

    @Test
    public void whenDeletingValidNumberReturns200() throws Exception {
        when(fireStationService.doesNumberOrAddressExists("44")).thenReturn(true);
        mockMvc.perform(delete("/firestation/44")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("List of stations with number or address 44 have been removed successfully")));
    }

    @Test
    public void whenDeletingUnknownNumberReturns404() throws Exception {
        when(fireStationService.doesNumberOrAddressExists("44")).thenReturn(false);
        mockMvc.perform(delete("/firestation/44")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No station found with number or address 44")));
    }

    @Test
    public void whenDeletingUnknownAddressReturns404() throws Exception {
        when(fireStationService.doesNumberOrAddressExists("1 Fake St.")).thenReturn(false);
        mockMvc.perform(delete("/firestation/1 Fake St.")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No station found with number or address 1 Fake St.")));
    }

    @Test
    public void whenNoParamsReturns404() throws Exception {
        mockMvc.perform(delete("/firestation/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenUpdateWithUnknownStationReturns404() throws Exception {
        FireStation fireStation = new FireStation("11 rue de Paris", "34");
        when(fireStationService.doesNumberOrAddressExists("11 rue de Paris")).thenReturn(false);
        when(fireStationService.doesStationAlreadyExist(fireStation.getAddress(), fireStation.getStation())).thenReturn(false);
        mockMvc.perform(put("/firestation")
                        .content(AlertsUtility.convertObjectToString(fireStation))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No station found with address 11 rue de Paris")));
    }

    @Test
    public void whenUpdateWithExistingStationReturns204() throws Exception {
        FireStation validStation34 = new FireStation("11 rue de Paris", "34");
        when(fireStationService.doesNumberOrAddressExists("11 rue de Paris")).thenReturn(true);
        when(fireStationService.doesStationAlreadyExist("11 rue de Paris", "34")).thenReturn(true);
        mockMvc.perform(put("/firestation")
                        .content(AlertsUtility.convertObjectToString(validStation34))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));
    }

    @Test
    public void whenUpdateWithValidStationReturns200() throws Exception {
        FireStation validStation34 = new FireStation("11 rue de Paris", "34");
        when(fireStationService.doesNumberOrAddressExists("11 rue de Paris")).thenReturn(true);
        when(fireStationService.doesStationAlreadyExist("11 rue de Paris", "34")).thenReturn(false);
        when(fireStationService.update(validStation34)).thenReturn(validStation34);
        mockMvc.perform(put("/firestation")
                        .content(AlertsUtility.convertObjectToString(validStation34))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    public void whenUpdateWithMalformedStationReturns400() throws Exception {
        FireStation newStation = new FireStation("rue de Paris", "344");
        mockMvc.perform(put("/firestation")
                        .content(AlertsUtility.convertObjectToString(newStation))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("must start with a digit and can contain only spaces, '.', digits or letters. Length must be minimum 5 characters")))
                .andExpect((jsonPath("$.message", containsString("number must be positive with maximum 2 digits whose minimum value starts at 1"))));
    }

}
