package net.safety.alerts.integration;

import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.service.CommunityEmailService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CommunityEmailIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommunityEmailService communityEmailService;

    @BeforeAll
    public static void setUpDataSource() throws IOException {
     AlertsDAO.setFilePath("src/test/resources/data-test-source.json");
    }

    @AfterAll
    public static void rollbackDataSource() throws IOException {
        AlertsDAO.setFilePath("src/main/resources/data.json");
    }
    @ParameterizedTest(name = "Testing city name -> \"{0}\" should return 200 and a list of 23 emails.")
    @ValueSource(strings={"Culver", "culver", "culveR"})
    public void testCommunityEmailControllerReturns200(String city) throws Exception {
        mockMvc.perform(get("/communityEmail?city="+city))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(23)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]", containsString("jaboyd@email.com")));
    }

    @ParameterizedTest(name = "Testing city name -> \"{0}\" should return 404.")
    @ValueSource(strings={"Los Angeles", "New York", "Miami","Cul ver", " culver"})
    public void testCommunityEmailControllerReturns400(String city) throws Exception {
        mockMvc.perform(get("/communityEmail?city="+city))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(String.format("City name %s not found in the list of person's address.", city))));
    }

    @Test
    public void testNullParamReturns400() throws Exception {
        mockMvc.perform(get("/communityEmail?city="))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("city name must not be null or empty.")));
    }


}
