package net.safety.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.safety.alerts.model.Person;
import net.safety.alerts.service.PersonService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    private static Person malformedPerson;

    private static Person rightPerson;

    private final static ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public static void setUp(){
        malformedPerson = new Person("P2", "L", "1 st Palace", "Los Angeles2", "456126", "222-666-888", "trgu.com");
        rightPerson = new Person("Jo-Jo", "Palmas", "123 Jojo ave","New York", "45678", "123-456-7894","jojo@yahoo.com");
    }


    @Test
    public void testingMalformedPersonReturns400() throws Exception {
        mockMvc.perform(post("/person")
                .content(mapper.writeValueAsString(malformedPerson))
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
    public void whenPassingNullPersonObjectReturns400() throws Exception {
        mockMvc.perform(post("/person")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("firstName: cannot be null")))
                .andExpect(jsonPath("$.message", containsString("lastName: cannot be null")));
    }

    @Test
    public void testingWellformedPersonReturns201() throws Exception {
        when(personService.doesPersonAlreadyExist(rightPerson)).thenReturn(false);
        when(personService.save(rightPerson)).thenReturn(rightPerson);
        mockMvc.perform(post("/person")
                        .content(mapper.writeValueAsString(rightPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.firstName",is("Jo-Jo")))
                .andExpect(jsonPath("$.lastName",is("Palmas")))
                .andExpect(jsonPath("$.city",is("New York")))
                .andExpect(jsonPath("$.address",is("123 Jojo ave")));
    }

    @Test
    public void postExistingPersonReturns204() throws Exception {
        when(personService.doesPersonAlreadyExist(rightPerson)).thenReturn(true);
        when(personService.save(rightPerson)).thenReturn(rightPerson);
        mockMvc.perform(post("/person")
                        .content(mapper.writeValueAsString(rightPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));
    }

    @Test
    public void updateNonExistingPersonReturns404() throws Exception {
        when(personService.doesPersonAlreadyExist(rightPerson)).thenReturn(false);
       mockMvc.perform(put("/person")
                        .content(mapper.writeValueAsString(rightPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404))
               .andExpect(jsonPath("$.message", is("No person found with firstname Jo-Jo and lastname Palmas")));
    }

    @Test
    public void updateExistingPersonReturns200() throws Exception {
        when(personService.doesPersonAlreadyExist(rightPerson)).thenReturn(true);
        when(personService.update(rightPerson)).thenReturn(rightPerson);
        mockMvc.perform(put("/person")
                        .content(mapper.writeValueAsString(rightPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName",is("Jo-Jo")))
                .andExpect(jsonPath("$.lastName",is("Palmas")))
                .andExpect(jsonPath("$.city",is("New York")))
                .andExpect(jsonPath("$.address",is("123 Jojo ave")));
    }

    @Test
    public void updateMalFormedPersonReturns400() throws Exception {
        mockMvc.perform(put("/person")
                        .content(mapper.writeValueAsString(malformedPerson))
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
    public void deleteUnknownPersonReturns404() throws Exception {
        when(personService.doesPersonAlreadyExist(rightPerson)).thenReturn(false);
        mockMvc.perform(delete("/person")
                        .content(mapper.writeValueAsString(rightPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No person found with firstname Jo-Jo and lastname Palmas")));
    }

    @Test
    public void deleteMalFormedPersonReturns400() throws Exception {
        mockMvc.perform(delete("/person")
                        .content(mapper.writeValueAsString(malformedPerson))
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
    public void deleteValidPersonReturns200() throws Exception {
        when(personService.doesPersonAlreadyExist(rightPerson)).thenReturn(true);
        mockMvc.perform(delete("/person")
                        .content(mapper.writeValueAsString(rightPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Person with firstname Jo-Jo and lastname Palmas has been deleted")));

    }







}
