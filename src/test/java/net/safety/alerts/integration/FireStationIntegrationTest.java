package net.safety.alerts.integration;


import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.service.FireStationService;
import net.safety.alerts.utils.AlertsUtility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FireStationIntegrationTest {

    @Autowired
    private FireStationService fireStationService;

    @Autowired
    private MockMvc mockMvc;


    @BeforeAll
    public static void setUpDataSource() throws IOException {
        Files.copy(Paths.get("src/test/resources/data-test-source.json"), Paths.get("src/test/resources/data-test.json"), StandardCopyOption.REPLACE_EXISTING);
        AlertsDAO.setFilePath("src/test/resources/data-test.json");
    }

    @AfterAll
    public static void rollbackDataSource() throws IOException {
        Files.delete(Paths.get("src/test/resources/data-test.json"));
        AlertsDAO.setFilePath("src/main/resources/data.json");
    }

    @Test
    @DisplayName("Check station number 1 returns a json with Person list, nbAdults and nbChidren infos.")
    public void testingResponseSuccessWithStationNumberOne() throws Exception {
        mockMvc.perform(get("/firestation?stationNumber=1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persons", hasSize(6)))
                .andExpect(jsonPath("$.nbAdults", is(5)))
                .andExpect(jsonPath("$.nbChildren", is(1)));
    }

    @Test
    @DisplayName("Check station number 6 returns a 404 status code.")
    public void testingCodeNotFoundWithStationNumberSix() throws Exception {
        mockMvc.perform(get("/firestation?stationNumber=6"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Station number 6 does not exist.")));
    }

    @ParameterizedTest(name = "Wrong parameter \"{0}\" for station number leads to bad request response.")
    @ValueSource(strings={"0","-1","s","","0.0","01","$1"})
    public void testingBadRequestWithStationNumberEmpty(String stationNumber) throws Exception {
        mockMvc.perform(get("/firestation?stationNumber="+stationNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",containsString("number must be positive with maximum 2 digits whose minimum value starts at 1")));

    }

    @Test
    public void createStationReturns200() throws Exception {
        FireStation validStation = new FireStation("10th downing street", "99");
        mockMvc.perform(post("/firestation")
                .content(AlertsUtility.convertObjectToString(validStation))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.address", is("10th downing street")))
                .andExpect(jsonPath("$.station", is("99")));
    }

    @Test
    public void createInValidStationReturns400() throws Exception {
        FireStation validStation = new FireStation("Th downing street", "9966");
        mockMvc.perform(post("/firestation")
                        .content(AlertsUtility.convertObjectToString(validStation))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", containsString("must start with a digit and can contain only spaces, '.', digits or letters. Length must be minimum 5 characters.")))
                .andExpect(jsonPath("$.message", containsString("number must be positive with maximum 2 digits whose minimum value starts at 1")));
    }

    @Test
    public void createEmptyStationReturns400() throws Exception {
        FireStation validStation = new FireStation("", "");
        mockMvc.perform(post("/firestation")
                        .content(AlertsUtility.convertObjectToString(validStation))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", containsString("must start with a digit and can contain only spaces, '.', digits or letters. Length must be minimum 5 characters.")))
                .andExpect(jsonPath("$.message", containsString("number must be positive with maximum 2 digits whose minimum value starts at 1")));
    }

    @Test
    public void createEmptyJsonReturns400() throws Exception {
        mockMvc.perform(post("/firestation")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message", containsString("cannot be null")))
                .andExpect(jsonPath("$.message", containsString(" cannot be null")));
    }

    @Test
    public void whenDeletingUnknownNumberReturns404() throws Exception {
        mockMvc.perform(delete("/firestation/77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No station found with number or address 77")));
    }

    @Test
    public void whenDeletingUnknownAddressReturns404() throws Exception {

        mockMvc.perform(delete("/firestation/12 th North ave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No station found with number or address 12 th North ave")));
    }

    @Test
    public void whenDeletingValidStationByNumberReturns200() throws Exception {
        FireStation validStation = new FireStation("2 rue de Paris", "55");
        mockMvc.perform(post("/firestation")
                .content(AlertsUtility.convertObjectToString(validStation))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        mockMvc.perform(delete("/firestation/55")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("List of stations with number or address 55 have been removed successfully")));
    }
    @Test
    public void whenDeletingValidStationByAddressReturns200() throws Exception {
        FireStation validStation = new FireStation("6 rue de Paris", "45");
        mockMvc.perform(post("/firestation")
                .content(AlertsUtility.convertObjectToString(validStation))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        mockMvc.perform(delete("/firestation/6 rue de Paris")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("List of stations with number or address 6 rue de Paris have been removed successfully")));
    }

    @Test
    public void whenUpdateWithUnknownStationReturns404() throws Exception {
        mockMvc.perform(put("/firestation/1100 rue de Paris/stationNumber=34")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No station found with address 1100 rue de Paris")));
    }

    @Test
    public void whenUpdateWithValidStationReturns200() throws Exception {
        FireStation validStation34 = new FireStation("11 rue de Paris", "34");
        mockMvc.perform(put("/firestation/748 Townings Dr/stationNumber=34")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", is("748 Townings Dr")))
                .andExpect(jsonPath("$.station", is("34")));
    }






}
