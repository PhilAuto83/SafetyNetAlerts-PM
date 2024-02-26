package net.safety.alerts.controller;

import net.safety.alerts.exceptions.CityNotFoundException;
import net.safety.alerts.service.CommunityEmailService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommunityEmailController.class)
public class CommunityEmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommunityEmailService communityEmailService;

    private static List<String> emails;

    @BeforeAll
    public static void setUpData(){
        emails = List.of("test1@gmail.com", "test2@yahoo.fr", "test3@free.fr");
    }

    @Test
    public void testCommunityEmailControllerReturns200() throws Exception {
        when(communityEmailService.getEmailsFromCity("Culver")).thenReturn(emails);
        mockMvc.perform(get("/communityEmail?city=Culver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("test1@gmail.com")));
    }

    @ParameterizedTest(name = "Testing city name -> \"{0}\" should return 400.")
    @ValueSource(strings={"C", "C1", "1c", "", " ", " paris", "!Culver"})
    public void testCommunityEmailControllerReturns400(String city) throws Exception {
        when(communityEmailService.getEmailsFromCity("Culver")).thenReturn(emails);
        mockMvc.perform(get("/communityEmail?city="+city))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("city name must have at least 2 letters.")));
    }

    @Test
    public void testThrowingCityNotFoundExceptionReturns404() throws Exception {
        when(communityEmailService.getEmailsFromCity("Culver")).thenThrow(CityNotFoundException.class);
        mockMvc.perform(get("/communityEmail?city=Culver"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testNoRequestParamReturns400() throws Exception {
        when(communityEmailService.getEmailsFromCity("Culver")).thenThrow(CityNotFoundException.class);
        mockMvc.perform(get("/communityEmail"))
                .andExpect(status().isBadRequest());
    }
}
