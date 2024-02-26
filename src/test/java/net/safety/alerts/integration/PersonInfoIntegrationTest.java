package net.safety.alerts.integration;

import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.service.PersonInfoService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonInfoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonInfoService personInfoService;

    @BeforeAll
    public static void setUpDataSource(){
        AlertsDAO.setFilePath("src/test/resources/data-test.json");
    }

    @AfterAll
    public static void rollbackDataSource(){
        AlertsDAO.setFilePath("src/main/resources/data.json");
    }

    @ParameterizedTest(name = "When searching with null firstname and for lastname {0}, result should be 200.")
    @ValueSource(strings={"Boyd", "BOYD", "boyd", "bOyd"})
    public void testingPersonInfoControllerWithDifferentLastNamesReturns200(String lastName) throws Exception {
        mockMvc.perform(get("/personInfo?lastName="+lastName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @ParameterizedTest(name = "When searching with empty firstname and lastname {0}, result should be 200.")
    @ValueSource(strings={"Boyd", "BOYD", "boyd", "bOyd"})
    public void testingPersonInfoControllerWithDifferentLastNamesAndEmptyFirstNameReturns200(String lastName) throws Exception {
        mockMvc.perform(get("/personInfo?firstName&lastName="+lastName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @ParameterizedTest(name = "When searching with firstname {0} and lastname {1}, result should be 200.")
    @CsvSource({"Allison, Boyd", "allison, BOYD", "allisOn, boyd", "ALLISON, bOyd"})
    public void testingPersonInfoControllerWithDifferentLastNamesAndEmptyFirstNameReturns200(String firstName, String lastName) throws Exception {
        mockMvc.perform(get("/personInfo?firstName="+firstName+"&lastName="+lastName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName", is("Allison Boyd")))
                .andExpect(jsonPath("$[0].medicalInfos.allergies[0]", is("nillacilan")))
                .andExpect(jsonPath("$[0].medicalInfos.medications[0]", is("aznol:200mg")))
                .andExpect(jsonPath("$[0].email", is("aly@imail.com")));
    }


    @ParameterizedTest(name= "Firstname empty and wrong lastname \"{0}\" leads to bad request")
    @ValueSource(strings={"", " ", "12", "1Bob", ".?"})
    public void testingWithEmptyFirstNameAndWrongLastNameReturns400(String lastName) throws Exception {
        mockMvc.perform(get("/personInfo?firstName=&lastName="+lastName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("lastname must be at least 2 characters long with letters only")));
    }

    @ParameterizedTest(name= "Lastname only with parameter \"{0}\" leads to bad request")
    @ValueSource(strings={"", " ", "12", "1Bob", ".?"})
    public void testingPersonInfoControllerWithEmptyFirstNameAndWrongLastNameReturns400(String lastName) throws Exception {
        mockMvc.perform(get("/personInfo?lastName="+lastName))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("lastname must be at least 2 characters long with letters only")));;
    }
}