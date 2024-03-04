package net.safety.alerts.integration;

import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.service.ChildAlertService;
import net.safety.alerts.service.FireStationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ChildAlertIntegrationTest {

    @Autowired
    private ChildAlertService childAlertService;

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
    @DisplayName("Check address '1509 Culver St' returns 2 children and 3 other members.")
    public void testingResponseSuccessWithStationNumberOne() throws Exception {
        mockMvc.perform(get("/childAlert?address=1509 Culver St"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.childrenList", hasSize(2)))
                .andExpect(jsonPath("$.otherMembers", hasSize(3)));
    }

    @Test
    @DisplayName("Check unknown address '1510 Culver St' returns 404 not found.")
    public void testingUnKnownAddressReturns404() throws Exception {
        mockMvc.perform(get("/childAlert?address=1510 Culver St"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Check empty address returns 400 bad request.")
    public void testingEmptyAddressReturns400() throws Exception {
        mockMvc.perform(get("/childAlert?address="))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
