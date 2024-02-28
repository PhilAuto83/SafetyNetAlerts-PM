package net.safety.alerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.model.Person;
import net.safety.alerts.service.PersonService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    private final static ObjectMapper mapper = new ObjectMapper();

    private Person validPerson;

    private Person invalidPerson;

    @BeforeAll
    public static void setUpDataSource(){
        AlertsDAO.setFilePath("src/test/resources/data-test.json");
    }
    @BeforeEach
    public void setUpPersons(){
        validPerson = new Person("Phil", "Valid", "121 Candy St.",
                "Philadelphia", "78945","111-222-9999","phil@test.fr");
        invalidPerson = new Person("phil", "1Valid", "121 Candy St.$",
                "", "-45678","222-9999","phil@test?fr");
    }

    @AfterAll
    public static void rollbackDataSource(){
        AlertsDAO.setFilePath("src/main/resources/data.json");
    }

    @Test
    public void createValidPersonReturns200() throws Exception {
        mockMvc.perform(post("/person")
                        .content(mapper.writeValueAsString(validPerson))
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
        Person validPerson2 = new Person("Philip", "Valid", "122 Candy St.",
                "New York", "78946","111-222-9990","philip@test.fr");
        mockMvc.perform(post("/person")
                .content(mapper.writeValueAsString(validPerson2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        validPerson2.setCity("Paris");
        validPerson2.setAddress("11 rue Marie Curie Paris");
        mockMvc.perform(put("/person")
                        .content(mapper.writeValueAsString(validPerson2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.firstName",is("Philip")))
                .andExpect(jsonPath("$.lastName",is("Valid")))
                .andExpect(jsonPath("$.city",is("Paris")))
                .andExpect(jsonPath("$.address",is("11 rue Marie Curie Paris")))
                .andExpect(jsonPath("$.phone",is("111-222-9990")))
                .andExpect(jsonPath("$.email",is("philip@test.fr")))
                .andExpect(jsonPath("$.zip",is("78946")));
    }

    @Test
    public void updateUnknownPersonReturns404() throws Exception {
        Person unknown = new Person("Fail", "Fail",
                "11 rue du fail", "Failcity", "45678","444-555-6987","fail@test.fr");
        mockMvc.perform(put("/person")
                        .content(mapper.writeValueAsString(unknown))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No person found with firstname Fail and lastname Fail")));
    }

    @Test
    public void deleteUnknownPersonReturns404() throws Exception {
        Person unknown = new Person("Fail", "Fail",
                "11 rue du fail", "Failcity", "45678","444-555-6987","fail@test.fr");
        mockMvc.perform(delete("/person")
                        .content(mapper.writeValueAsString(unknown))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No person found with firstname Fail and lastname Fail")));
    }
    @Test
    public void deletePersonReturns200() throws Exception {
        mockMvc.perform(delete("/person")
                        .content(mapper.writeValueAsString(validPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Person with firstname Phil and lastname Valid has been deleted")));
    }

    @Test
    public void testingInvalidPersonReturns400() throws Exception {
        mockMvc.perform(post("/person")
                        .content(mapper.writeValueAsString(invalidPerson))
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
                .content(mapper.writeValueAsString(wemby))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));

    }
}
