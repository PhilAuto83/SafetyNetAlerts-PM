package net.safety.alerts.controller;


import net.safety.alerts.dto.FireDTO;
import net.safety.alerts.dto.PersonMedicalDataDTO;
import net.safety.alerts.exceptions.AddressNotFoundException;
import net.safety.alerts.exceptions.StationNumberNotFoundException;
import net.safety.alerts.service.FireService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FireController.class)
public class FireControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FireService fireService;

    private static FireDTO fireDTO;

    @BeforeAll
    public static void setUp(){
        Map<String, List<String>> medicalInfos = new HashMap<>();
        medicalInfos.put("medications",List.of("hydrapermazol:300mg", "dodoxadin:30mg"));
        medicalInfos.put("allergies", List.of("peanut","nillacilan"));

        fireDTO = new FireDTO("4", List.of(new PersonMedicalDataDTO("Phil Test"
                ,"555-777-9999", 45, medicalInfos)));
    }

    @Test
    public void testFireControllerReturns200() throws Exception {
        when(fireService.getPersonMedicalInfoByAddress("834 Binoc Ave")).thenReturn(fireDTO);
        mockMvc.perform(get("/fire?address=834 Binoc Ave"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persons", hasSize(1)))
                .andExpect(jsonPath("$.stationNumber", is("4")))
                .andExpect(jsonPath("$.persons[0].phone", is("555-777-9999")))
                .andExpect(jsonPath("$.persons[0].age", is(45)))
                .andExpect(jsonPath("$.persons[0].medicalInfos.allergies[0]", is("peanut")));
    }

    @Test
    public void testFireControllerWithUnknownAddressReturns404() throws Exception {
        when(fireService.getPersonMedicalInfoByAddress("834 Binoc Ave")).thenThrow(StationNumberNotFoundException.class);
        mockMvc.perform(get("/fire?address=834 Binoc Ave"))
                .andExpect(status().isNotFound());


    }
    @ParameterizedTest(name="Test FireController with value \"{0}\" leads to 400 bad request.")
    @ValueSource(strings={"", " "})
    public void testFireControllerWithNullOrEmptyValueReturns400(String address) throws Exception {
        mockMvc.perform(get("/fire?address="))
                .andExpect(status().isBadRequest());
    }


}
