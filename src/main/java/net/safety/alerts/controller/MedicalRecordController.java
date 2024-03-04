package net.safety.alerts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Valid;
import net.safety.alerts.exceptions.MedicalRecordNotFoundException;
import net.safety.alerts.exceptions.MedicationOrAllergyFormatException;
import net.safety.alerts.exceptions.PersonNotFoundException;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.model.Person;
import net.safety.alerts.service.MedicalRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class MedicalRecordController {

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);

    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private MedicalRecordService medicalRecordService;

    @PostMapping(value = "/medicalRecord", produces = {"application/json"}, consumes={"application/json"})
    public ResponseEntity<MedicalRecord> create(@Valid @RequestBody  MedicalRecord medicalRecord) throws MedicationOrAllergyFormatException, JsonProcessingException {
        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();
        logger.info("Request to add a medical record launched : {}", currentUri);
        logger.info("Request payload : {}", mapper.writeValueAsString(medicalRecord));

        if(!medicalRecordService.isMedicationListValid(medicalRecord.getMedications())
                || !medicalRecordService.isAllergyListValid(medicalRecord.getAllergies())){
            logger.error("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg");
            throw new MedicationOrAllergyFormatException("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg");
        }else if(medicalRecordService.doesMedicalRecordExist(medicalRecord)){
            logger.debug("Medical record already exists with firstname {} and lastname {}", medicalRecord.getFirstName(), medicalRecord.getLastName());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(medicalRecordService.save(medicalRecord));
    }

    @PutMapping(value = "/medicalRecord", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<MedicalRecord> update(@Valid @RequestBody MedicalRecord medicalRecord) throws JsonProcessingException, MedicalRecordNotFoundException {
        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();
        logger.info("Request to update a medical record launched : {}", currentUri);
        logger.info("Request payload : {}", mapper.writeValueAsString(medicalRecord));
        if(!medicalRecordService.isMedicationListValid(medicalRecord.getMedications())
                || !medicalRecordService.isAllergyListValid(medicalRecord.getAllergies())){
            logger.error("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg");
            throw new MedicationOrAllergyFormatException("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg");
        }else if(!medicalRecordService.doesMedicalRecordExist(medicalRecord)){
            logger.error("Medical record does not exist with firstname {} and lastname {}", medicalRecord.getFirstName(), medicalRecord.getLastName());
            throw new MedicalRecordNotFoundException(String.format("Medical record does not exist with firstname %s and lastname %s", medicalRecord.getFirstName(), medicalRecord.getLastName()));
        }
        return ResponseEntity.ok(medicalRecordService.update(medicalRecord));
    }
}
