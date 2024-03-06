package net.safety.alerts.integration;

import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.service.FireStationService;
import net.safety.alerts.service.FloodService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FloodIntegrationTest {

    @Autowired
    private FireStationService fireStationService;
    @Autowired
    private FloodService floodService;
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
    public void testStation4Returns200WithInfos() throws Exception {
        mockMvc.perform(get("/flood/stations?stations=4"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].address", is("489 Manchester St")))
                .andExpect(jsonPath("$.[0].persons[0].age", is(30)))
                .andExpect(jsonPath("$.[0].persons[0].phone", is("841-874-9845")))
                .andExpect(jsonPath("$.[0].persons[0].fullName", is("Lily Cooper")))
                .andExpect(jsonPath("$[1].persons[0].medicalInfos.allergies[0]", is("shellfish")));
    }

    @Test
    public void testUnknownStationNumberReturns404() throws Exception {
        mockMvc.perform(get("/flood/stations?stations=6"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Station number 6 does not exist.")));
    }

    @ParameterizedTest(name="Wrong argument \"{0}\" returns a bad request.")
    @ValueSource(strings={"", " ", "-1", "0", "test"})
    public void testWrongArgumentStationNumberReturns400(String stationNumber) throws Exception {
        mockMvc.perform(get("/flood/stations?stations="+stationNumber))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("must be positive with maximum 2 digits whose minimum value starts at 1")));
    }

}
