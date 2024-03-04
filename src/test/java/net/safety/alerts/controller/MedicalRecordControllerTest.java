package net.safety.alerts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(MedicalRecordController.class)
public class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MedicalRecordService medicalRecordService;

    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static MedicalRecord validMedicalRecord;
    private static MedicalRecord inValidMedicalRecord;
    @BeforeAll
    public static void setUpData() {
        validMedicalRecord = new MedicalRecord("Testing", "Test", LocalDate.of(1990, 2, 10), List.of("dolip:500mg"), List.of("nuts"));

        inValidMedicalRecord = new MedicalRecord("Testing2", "Test", LocalDate.of(1990, 2, 10), List.of("dolip:500mg"), List.of("nuts"));
    }

    @Test
    public void whenValidMedicalRecordThenReturns200() throws Exception {
        when(medicalRecordService.isMedicationListValid(validMedicalRecord.getMedications())).thenReturn(true);
        when(medicalRecordService.isAllergyListValid(validMedicalRecord.getAllergies())).thenReturn(true);
        when(medicalRecordService.doesMedicalRecordExist(validMedicalRecord)).thenReturn(false);
        when(medicalRecordService.save(validMedicalRecord)).thenReturn(validMedicalRecord);
        mockMvc.perform(post("/medicalRecord")
                .content(mapper.writeValueAsString(validMedicalRecord))
                .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.medications[0]", is("dolip:500mg")));
    }

    @Test
    public void whenInValidMedicalRecordThenReturns200() throws Exception {
              mockMvc.perform(post("/medicalRecord")
                        .content(mapper.writeValueAsString(inValidMedicalRecord))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("should contain only letters, '-' or space, start with capital letter, have minimum two characters and match following examples  Li An, Li-An or Lo")));
    }
    @Test
    public void whenPersonHasAlreadyRecordThenReturns204() throws Exception {
        when(medicalRecordService.isMedicationListValid(validMedicalRecord.getMedications())).thenReturn(true);
        when(medicalRecordService.isAllergyListValid(validMedicalRecord.getAllergies())).thenReturn(true);
        when(medicalRecordService.doesMedicalRecordExist(validMedicalRecord)).thenReturn(true);
        mockMvc.perform(post("/medicalRecord")
                        .content(mapper.writeValueAsString(validMedicalRecord))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(204));
    }

    @Test
    public void whenInvalidMedicalRecordFormatThenReturns400() throws Exception {
        when(medicalRecordService.isMedicationListValid(validMedicalRecord.getMedications())).thenReturn(false);
        when(medicalRecordService.isAllergyListValid(validMedicalRecord.getAllergies())).thenReturn(true);
        mockMvc.perform(post("/medicalRecord")
                        .content(mapper.writeValueAsString(validMedicalRecord))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg")));
    }


    @Test
    public void whenInvalidAllergyRecordFormatThenReturns400() throws Exception {
        when(medicalRecordService.isMedicationListValid(validMedicalRecord.getMedications())).thenReturn(true);
        when(medicalRecordService.isAllergyListValid(validMedicalRecord.getAllergies())).thenReturn(false);
        mockMvc.perform(post("/medicalRecord")
                        .content(mapper.writeValueAsString(validMedicalRecord))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg")));
    }

    @Test
    public void testingNullValuesForAllergiesAndMedicationsReturns200() throws Exception {
        MedicalRecord nullAllergiesAndMedications = new MedicalRecord("Testing", "Test", LocalDate.of(1990,2, 10), null, null);
        when(medicalRecordService.isMedicationListValid(nullAllergiesAndMedications.getMedications())).thenReturn(true);
        when(medicalRecordService.isAllergyListValid(nullAllergiesAndMedications.getAllergies())).thenReturn(true);
        when(medicalRecordService.doesMedicalRecordExist(nullAllergiesAndMedications)).thenReturn(false);
        when(medicalRecordService.save(nullAllergiesAndMedications)).thenReturn(nullAllergiesAndMedications);
        mockMvc.perform(post("/medicalRecord")
                        .content(mapper.writeValueAsString(nullAllergiesAndMedications))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.birthDate", is("02/10/1990")));
    }

    @Test
    public void testingEmptyValuesForAllergiesAndMedicationsReturns200() throws Exception {
        MedicalRecord emptyAllergiesAndMedications = new MedicalRecord("Testing", "Test", LocalDate.of(1990,2, 10), List.of(""), List.of(""));
        when(medicalRecordService.isMedicationListValid(emptyAllergiesAndMedications.getMedications())).thenReturn(true);
        when(medicalRecordService.isAllergyListValid(emptyAllergiesAndMedications.getAllergies())).thenReturn(true);
        when(medicalRecordService.doesMedicalRecordExist(emptyAllergiesAndMedications)).thenReturn(false);
        when(medicalRecordService.save(emptyAllergiesAndMedications)).thenReturn(emptyAllergiesAndMedications);
        mockMvc.perform(post("/medicalRecord")
                        .content(mapper.writeValueAsString(emptyAllergiesAndMedications))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.birthDate", is("02/10/1990")));
    }

    @Test
    public void testingWrongDateFormatReturns400() throws Exception {

        mockMvc.perform(post("/medicalRecord")
                        .content("{\"firstName\":\"Phil\"," +
                                "{\"lastName\":\"Test\"," +
                                "{\"birthDate\":\"18/10/1990\"}")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void EmptyObjectReturns400() throws Exception {
        mockMvc.perform(post("/medicalRecord")
                        .content("{}")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenMedicalRecordNotFoundThenReturns404() throws Exception {
        when(medicalRecordService.isMedicationListValid(validMedicalRecord.getMedications())).thenReturn(true);
        when(medicalRecordService.isAllergyListValid(validMedicalRecord.getAllergies())).thenReturn(true);
        when(medicalRecordService.doesMedicalRecordExist(validMedicalRecord)).thenReturn(false);
        mockMvc.perform(put("/medicalRecord")
                        .content(mapper.writeValueAsString(validMedicalRecord))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Medical record does not exist with firstname Testing and lastname Test")));
    }

    @Test
    public void whenUpdatingMedicalRecordFoundThenReturns200() throws Exception {
        when(medicalRecordService.isMedicationListValid(validMedicalRecord.getMedications())).thenReturn(true);
        when(medicalRecordService.isAllergyListValid(validMedicalRecord.getAllergies())).thenReturn(true);
        when(medicalRecordService.doesMedicalRecordExist(validMedicalRecord)).thenReturn(true);
        mockMvc.perform(put("/medicalRecord")
                        .content(mapper.writeValueAsString(validMedicalRecord))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void whenUpdatingMedicalRecordWithWrongMedicationFormatThenReturns200() throws Exception {
        when(medicalRecordService.isMedicationListValid(inValidMedicalRecord.getMedications())).thenReturn(false);
        when(medicalRecordService.isAllergyListValid(inValidMedicalRecord.getAllergies())).thenReturn(true);
        when(medicalRecordService.doesMedicalRecordExist(inValidMedicalRecord)).thenReturn(true);
        mockMvc.perform(put("/medicalRecord")
                        .content(mapper.writeValueAsString(validMedicalRecord))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg")));
    }

    @Test
    public void whenUpdatingMedicalRecordWithWrongAllergyFormatThenReturns200() throws Exception {
        when(medicalRecordService.isMedicationListValid(inValidMedicalRecord.getMedications())).thenReturn(true);
        when(medicalRecordService.isAllergyListValid(inValidMedicalRecord.getAllergies())).thenReturn(false);
        when(medicalRecordService.doesMedicalRecordExist(inValidMedicalRecord)).thenReturn(true);
        mockMvc.perform(put("/medicalRecord")
                        .content(mapper.writeValueAsString(validMedicalRecord))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg")));
    }

    @Test
    public void whenDeletingMedicalRecordWithUnknownFirstNameAndLastNameThenReturns404() throws Exception {
        when(medicalRecordService.doesMedicalRecordExistWithFirstNameAndLastName("Tester", "Test")).thenReturn(false);
        mockMvc.perform(delete("/medicalRecord/Test/Tester")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Medical record does not exist with firstname Tester and lastname Test")));
    }

    @Test
    public void whenDeletingMedicalRecordWithKnownFirstNameAndLastNameThenReturns200() throws Exception {
        when(medicalRecordService.doesMedicalRecordExistWithFirstNameAndLastName("Tester", "Test")).thenReturn(true);
        when(medicalRecordService.remove("Tester", "Test")).thenReturn(true);
        mockMvc.perform(delete("/medicalRecord/Test/Tester")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Medical record with firstname Tester and lastname Test has been removed successfully")));
    }

    @Test
    public void whenDeletingMedicalRecordWithKnownFirstNameAndLastNameThenReturns400() throws Exception {
        when(medicalRecordService.doesMedicalRecordExistWithFirstNameAndLastName("Tester", "Test")).thenReturn(true);
        when(medicalRecordService.remove("Tester", "Test")).thenReturn(false);
        mockMvc.perform(delete("/medicalRecord/Test/Tester")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Medical record with firstname Tester and lastname Test was not removed correctly")));
    }
}
