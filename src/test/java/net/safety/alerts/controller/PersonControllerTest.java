package net.safety.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.model.Person;
import net.safety.alerts.service.PersonService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(jsonPath("$.message", containsString("must start with 2 letters and can contain whitespace or '-'")))
                .andExpect(jsonPath("$.message", containsString("should have 5 digits")))
                .andExpect(jsonPath("$.message", containsString("should contains only letters and minimum 2")))
                .andExpect(jsonPath("$.message", containsString("number should respect format example '123-456-9999'")))
                .andExpect(jsonPath("$.message", containsString("format is not valid, minimum format should be 'a@bc.fr'")));
    }

    @Test
    public void testingWellformedPersonReturns201() throws Exception {
        when(personService.doesPersonAlreadyExist(rightPerson)).thenReturn(false);
        mockMvc.perform(post("/person")
                        .content(mapper.writeValueAsString(rightPerson))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201));
    }

//    @Test
//    public void testingPersonAlreadyInFileReturns204() throws Exception {
//        when(personService.doesPersonAlreadyExist(rightPerson)).thenReturn(true);
//        mockMvc.perform(post("/person")
//                        .content(mapper.writeValueAsString(rightPerson))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(204));
//    }
}
