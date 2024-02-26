package net.safety.alerts.integration;

import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.service.FireStationService;
import net.safety.alerts.service.PhoneAlertService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PhoneAlertIntegrationTest {

    @Autowired
    private FireStationService fireStationService;

    @Autowired
    private PhoneAlertService phoneAlertService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void setUpDataSource(){
        AlertsDAO.setFilePath("src/test/resources/data-test.json");
    }

    @AfterAll
    public static void rollbackDataSource(){
        AlertsDAO.setFilePath("src/main/resources/data.json");
    }

    @ParameterizedTest(name = "Station number {0} returns 404.")
    @ValueSource(strings={"5","11"})
    public void whenRequestingStation5_thenReturn404(String stationNumber) throws Exception {
        mockMvc.perform(get("/phoneAlert?firestation="+stationNumber))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format("Station number %s does not exist.", stationNumber))));
    }

    @ParameterizedTest(name="When station number is {0} then returns 400 bad request.")
    @ValueSource(strings={"", " ","0", "-1"})
    public void whenRequestingEmptyStationNumber_thenReturn400(String stationNumber) throws Exception {
        mockMvc.perform(get("/phoneAlert?firestation="))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("station number must be a positive number with maximum 2 digits whose minimum value starts at 1")));;
    }

    @ParameterizedTest(name="When requesting station number {0} then return a list size of {1}")
    @CsvSource({"3, 7","1, 4"})
    public void whenRequestingStation4_thenReturn200WithPhoneList(String stationNumber, int expectedSize) throws Exception {
        mockMvc.perform(get("/phoneAlert?firestation="+stationNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedSize)));
    }
}
