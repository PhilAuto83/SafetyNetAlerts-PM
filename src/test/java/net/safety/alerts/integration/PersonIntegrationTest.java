package net.safety.alerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.model.Person;
import net.safety.alerts.service.PersonService;
import net.safety.alerts.utils.AlertsUtility;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PersonIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonService personService;


    private Person validPerson;

    private Person invalidPerson;

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
    @BeforeEach
    public void setUpPersons(){
        validPerson = new Person("Phil", "Valid", "121 Candy St.",
                "Philadelphia", "78945","111-222-9999","phil@test.fr");
        invalidPerson = new Person("phil", "1Valid", "121 Candy St.$",
                "", "-45678","222-9999","phil@test?fr");
    }

    @Test
    public void createValidPersonReturns200() throws Exception {
        mockMvc.perform(post("/person")
                        .content(AlertsUtility.convertObjectToString(validPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.firstName",is("Phil")))
                .andExpect(jsonPath("$.lastName",is("Valid")))
                .andExpect(jsonPath("$.city",is("Philadelphia")))
                .andExpect(jsonPath("$.address",is("121 Candy St.")))
                .andExpect(jsonPath("$.phone",is("111-222-9999")))
                .andExpect(jsonPath("$.email",is("phil@test.fr")))
                .andExpect(jsonPath("$.zip",is("78945")));
    }

    @Test
    public void updateValidPersonReturns200() throws Exception {
        validPerson.setCity("Paris");
        validPerson.setAddress("11 rue Marie Curie Paris");
        mockMvc.perform(put("/person")
                        .content(AlertsUtility.convertObjectToString(validPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.firstName",is("Phil")))
                .andExpect(jsonPath("$.lastName",is("Valid")))
                .andExpect(jsonPath("$.city",is("Paris")))
                .andExpect(jsonPath("$.address",is("11 rue Marie Curie Paris")))
                .andExpect(jsonPath("$.phone",is("111-222-9999")))
                .andExpect(jsonPath("$.email",is("phil@test.fr")))
                .andExpect(jsonPath("$.zip",is("78945")));
        mockMvc.perform(delete("/person")
                .content(AlertsUtility.convertObjectToString(validPerson))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    public void updateUnknownPersonReturns404() throws Exception {
        Person unknown = new Person("Fail", "Fail",
                "11 rue du fail", "Failcity", "45678","444-555-6987","fail@test.fr");
        mockMvc.perform(put("/person")
                        .content(AlertsUtility.convertObjectToString(unknown))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No person found with firstname Fail and lastname Fail")));
    }

    @Test
    public void deleteUnknownPersonReturns404() throws Exception {
        mockMvc.perform(delete("/person/Fail/Fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No person found with firstname Fail and lastname Fail")));
    }
    @Test
    public void deletePersonReturns200() throws Exception {
        Person validPerson3 = new Person("Joe", "Test",
                "11 rue du fake", "Joecity", "45674","444-555-7987","joe@test.fr");
        mockMvc.perform(post("/person")
                .content(AlertsUtility.convertObjectToString(validPerson3))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        mockMvc.perform(delete("/person/Joe/Test")
                        .content(AlertsUtility.convertObjectToString(validPerson3))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Person with firstname Joe and lastname Test has been deleted")));
    }

    @Test
    public void testingInvalidPersonReturns400() throws Exception {
        mockMvc.perform(post("/person")
                        .content(AlertsUtility.convertObjectToString(invalidPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("should contain only letters, '-' or space, start with capital letter, have minimum two characters and match following examples  Li An, Li-An or Lo")))
                .andExpect(jsonPath("$.message", containsString("should have 5 digits")))
                .andExpect(jsonPath("$.message", containsString("must start with capital letter and can contain whitespace following theses examples New York or Miami")))
                .andExpect(jsonPath("$.message", containsString("number should respect format example '123-456-9999'")))
                .andExpect(jsonPath("$.message", containsString("format is not valid")));
    }

    @Test
    public void postExistingPersonReturns204() throws Exception {
        Person wemby = new Person("Wemby", "Nba", "11 San Antonio St.",
                "San Antonio","45678","444-555-8888","spurs@center.com");
        mockMvc.perform(post("/person")
                .content(AlertsUtility.convertObjectToString(wemby))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));

    }
}
