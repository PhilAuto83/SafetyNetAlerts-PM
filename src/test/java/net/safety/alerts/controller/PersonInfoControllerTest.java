package net.safety.alerts.controller;

import net.safety.alerts.service.PersonInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PersonInfoController.class)
public class PersonInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonInfoService personInfoService;

    @Test
    public void testingPersonInfoControllerReturns200() throws Exception {
        mockMvc.perform(get("/personInfo?lastName=Boyd"))
                .andExpect(status().isOk());
    }

    @Test
    public void testingPersonInfoControllerWithEmptyFirstNameReturns200() throws Exception {
        mockMvc.perform(get("/personInfo?firstName=&lastName=Boyd"))
                .andExpect(status().isOk());
    }
}
