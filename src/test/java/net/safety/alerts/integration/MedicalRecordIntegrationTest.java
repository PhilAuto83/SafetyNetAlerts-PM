package net.safety.alerts.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.service.MedicalRecordService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MedicalRecordIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MedicalRecordService medicalRecordService;

    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static MedicalRecord validMedicalRecord2;
    private static MedicalRecord inValidMedicalRecord2;

    @BeforeAll
    public static void setUpDataSource() throws IOException {
        Files.copy(Paths.get("src/test/resources/data-test-source.json"), Paths.get("src/test/resources/data-test.json"), StandardCopyOption.REPLACE_EXISTING);
        AlertsDAO.setFilePath("src/test/resources/data-test.json");
        validMedicalRecord2 = new MedicalRecord("Testing", "Test", LocalDate.of(1990, 2, 10), List.of("dolip:500mg", "test:200ml"), List.of("nuts", "almonds"));
        inValidMedicalRecord2 = new MedicalRecord("Testing", "Test", LocalDate.of(1990, 2, 10), List.of("dolip:500m"), List.of("nut2"));

    }

    @AfterAll
    public static void rollbackDataSource() throws IOException {
        Files.delete(Paths.get("src/test/resources/data-test.json"));
        AlertsDAO.setFilePath("src/main/resources/data.json");
    }

    @Test
    public void testValidRecordReturns200() throws Exception {
        mockMvc.perform(post("/medicalRecord")
                        .content(mapper.writeValueAsString(validMedicalRecord2))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medications[0]", is("dolip:500mg")))
                .andExpect(jsonPath("$.medications[1]", is("test:200ml")))
                .andExpect(jsonPath("$.allergies[0]", is("nuts")))
                .andExpect(jsonPath("$.allergies[1]", is("almonds")))
                .andExpect(jsonPath("$.allergies", hasSize(2)))
                .andExpect(jsonPath("$.medications", hasSize(2)));
    }

    @Test
    public void testInValidMedicationFormatReturns400() throws Exception {
        mockMvc.perform(post("/medicalRecord")
                        .content(mapper.writeValueAsString(inValidMedicalRecord2))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg")));
    }

    @Test
    public void testInValidateSeparatorFormatReturns400() throws Exception {
        String request = "{\"firstName\": \"Phil\","+
                           "\"lastName\": \"Joe\","+
                           "\"birthDate\": \"11-01-1984\","+
                           "\"medications\": [\"ibu:20mg\"],"+
                          "\"allergies\": [\"strawberry\"]}";
        mockMvc.perform(post("/medicalRecord")
                        .content(request)
        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("date format must be MM/dd/yyyy")));
    }

    @Test
    public void testInValidateFormatReturns400() throws Exception {
        String request = "{\"firstName\": \"Phil\","+
                "\"lastName\": \"Joe\","+
                "\"birthDate\": \"13/01/1984\","+
                "\"medications\": [\"i:20mg\"],"+
                "\"allergies\": [\"strawberry\"]}";
        mockMvc.perform(post("/medicalRecord")
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("date format must be MM/dd/yyyy")));
    }

    @Test
    public void whenInvalidMedicalDoseFormatThenReturns400() throws Exception {
        MedicalRecord inValidMedicationDoseRecord = new MedicalRecord("Testing", "Test", LocalDate.of(1990,2, 10), List.of("dolip:50000mg"), List.of("nuts"));
        mockMvc.perform(post("/medicalRecord")
                        .content(mapper.writeValueAsString(inValidMedicationDoseRecord))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg")));
    }

    @Test
    public void userAlreadyExistsReturns204() throws Exception {
        MedicalRecord existingRecordWithFirstNameAndLastName = new MedicalRecord("John", "Boyd", LocalDate.of(1995,10,5), null, null);
        mockMvc.perform(post("/medicalRecord")
                        .content(mapper.writeValueAsString(existingRecordWithFirstNameAndLastName))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));
    }

    @Test
    public void whenUpdatingMedicalRecordFoundThenReturns200() throws Exception {
        MedicalRecord ericCadigan = new MedicalRecord("Eric", "Cadigan",LocalDate.of(1966, 3, 12),List.of("tradoxidine:2000mg"),List.of("apples"));
        mockMvc.perform(put("/medicalRecord")
                        .content(mapper.writeValueAsString(ericCadigan))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.birthDate", is("03/12/1966")))
                .andExpect(jsonPath("$.medications[0]", is("tradoxidine:2000mg")))
                .andExpect(jsonPath("$.allergies[0]", is("apples")))
                .andExpect(jsonPath("$.firstName", is("Eric")))
                .andExpect(jsonPath("$.lastName", is("Cadigan")));
    }

    @Test
    public void whenUpdatingMedicalRecordNotFoundThenReturns404() throws Exception {
        MedicalRecord ericUnknown = new MedicalRecord("Eric", "Unknown",LocalDate.of(1966, 3, 12),List.of("tradoxidine:2000mg"),List.of("apples"));
        mockMvc.perform(put("/medicalRecord")
                        .content(mapper.writeValueAsString(ericUnknown))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Medical record does not exist with firstname Eric and lastname Unknown")));

    }

    @Test
    public void whenUpdatingMedicalRecordWithTooLongMedicationValueThenReturns400() throws Exception {
        MedicalRecord longMedicationValue = new MedicalRecord("Eric", "Cadigan",LocalDate.of(1966, 3, 12),List.of("tradoxidineeeeeeeeeeeeee:2000mg"),List.of("apples"));

        mockMvc.perform(put("/medicalRecord")
                        .content(mapper.writeValueAsString(longMedicationValue))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg")));
    }

    @Test
    public void whenDeletingJohnBoydThenReturns200() throws Exception {

        mockMvc.perform(delete("/medicalRecord/Boyd/John")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Medical record with firstname John and lastname Boyd has been removed successfully")));
    }

    @Test
    public void whenDeletingJohnBoydUpperCaseThenReturns200() throws Exception {

        mockMvc.perform(delete("/medicalRecord/BOYD/JOHN")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Medical record with firstname JOHN and lastname BOYD has been removed successfully")));
    }

    @Test
    public void whenDeletingUnknownPersonThenReturns404() throws Exception {

        mockMvc.perform(delete("/medicalRecord/BOYD/JO")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Medical record does not exist with firstname JO and lastname BOYD")));
    }

}