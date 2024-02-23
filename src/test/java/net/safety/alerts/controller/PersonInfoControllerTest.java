package net.safety.alerts.controller;

import net.safety.alerts.dto.FloodDTO;
import net.safety.alerts.dto.PersonInfoDTO;
import net.safety.alerts.dto.PersonMedicalDataDTO;
import net.safety.alerts.service.PersonInfoService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonInfoController.class)
public class PersonInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonInfoService personInfoService;

    private static List<PersonInfoDTO> personInfoDTO;

    private static Map<String, List<String>> medicalData = new HashMap<>();

    @BeforeAll
    public static void setUp(){
        medicalData.put("medications", List.of("doliprane:500mg"));
        medicalData.put("allergies", List.of("peanut","oil"));
        personInfoDTO = List.of(new PersonInfoDTO("Phil Test", "1 st Test", 40, "phil@info.fr",medicalData));
    }

    @Test
    public void testingPersonInfoControllerWithNullFirstNameReturns200() throws Exception {
        when(personInfoService.doesPersonExists(null,"Boyd")).thenReturn(true);
        when(personInfoService.getPersonList(null,"Boyd")).thenReturn(personInfoDTO);
        mockMvc.perform(get("/personInfo?lastName=Boyd"))
                .andExpect(status().isOk());
    }

    @Test
    public void testingPersonInfoControllerWithFirstNameAndLastNameReturns200() throws Exception {
        when(personInfoService.doesPersonExists("Phil","Boyd")).thenReturn(true);
        when(personInfoService.getPersonList("Phil","Boyd")).thenReturn(personInfoDTO);
        mockMvc.perform(get("/personInfo?firstName=Phil&lastName=Boyd"))
                .andExpect(status().isOk());
    }

    @Test
    public void testingPersonInfoControllerWithEmptyFirstNameReturns200() throws Exception {
        when(personInfoService.doesPersonExists("","Boyd")).thenReturn(true);
        when(personInfoService.getPersonList("","Boyd")).thenReturn(personInfoDTO);
        mockMvc.perform(get("/personInfo?firstName=&lastName=Boyd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].address", is("1 st Test")))
                .andExpect(jsonPath("$.[0].age", is(40)))
                .andExpect(jsonPath("$.[0].email", is("phil@info.fr")))
                .andExpect(jsonPath("$.[0].medicalInfos.medications[0]", is("doliprane:500mg")))
                .andExpect(jsonPath("$.[0].medicalInfos.allergies[1]", is("oil")));
    }

    @ParameterizedTest(name= "Wrong lastname \"{0}\" leads to bad request")
    @ValueSource(strings={"", " ", "12", "1Bob", ".?"})
    public void testingPersonInfoControllerWithEmptyFirstNameAndWrongLastNameReturns400(String lastName) throws Exception {
        mockMvc.perform(get("/personInfo?firstName=&lastName="+lastName))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testingPersonInfoControllerWithoutParamsReturns400() throws Exception {
        mockMvc.perform(get("/personInfo"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testingPersonInfoControllerWithUnknownLastNameReturns404() throws Exception {
        when(personInfoService.doesPersonExists(null, "By")).thenReturn(false);
        mockMvc.perform(get("/personInfo?firstName=&lastName=By"))
                .andExpect(status().isNotFound());
    }
}
