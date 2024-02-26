package net.safety.alerts.controller;

import net.safety.alerts.service.CommunityEmailService;
import org.junit.jupiter.api.BeforeAll;
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

    public void testCommunityEmailControllerReturns200() throws Exception {
        when(communityEmailService.getEmailsFromCity("Culver")).thenReturn(emails);
        mockMvc.perform(get("/communityEmail?city=Culver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("test1@gmail.com")));
    }
}
