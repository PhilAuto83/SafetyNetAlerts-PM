package net.safety.alerts.integration;

import net.safety.alerts.dao.AlertsDAO;

import net.safety.alerts.service.FireService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FireIntegrationTest {

    @Autowired
    private FireService fireService;

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
    public void testingFireControllerReturns200WithAllInfos() throws Exception {
        mockMvc.perform(get("/fire?address=834 Binoc Ave"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stationNumber", is("3")))
                .andExpect(jsonPath("$.persons", hasSize(1)))
                .andExpect(jsonPath("$.persons[0].fullName", is("Tessa Carman")))
                .andExpect(jsonPath("$.persons[0].age", is(12)))
                .andExpect(jsonPath("$.persons[0].medicalInfos.medications", hasSize(0)))
                .andExpect(jsonPath("$.persons[0].medicalInfos.allergies", hasSize(0)));
    }

    @Test
    public void testingFireControllerReturns404ForWrongAddress() throws Exception {
        mockMvc.perform(get("/fire?address=834 Binoc Av"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Station number does not exist at address : 834 Binoc Av")));
    }

    @Test
    public void testingFireControllerReturns400ForBlankAddress() throws Exception {
        mockMvc.perform(get("/fire?address="))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("address cannot be null or empty")));
    }

    @Test
    public void testingFireControllerReturns400ForEmptyAddress() throws Exception {
        mockMvc.perform(get("/fire?address= "))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("address cannot be null or empty")));
    }
}
