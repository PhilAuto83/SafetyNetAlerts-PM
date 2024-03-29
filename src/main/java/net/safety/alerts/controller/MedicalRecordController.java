package net.safety.alerts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import net.safety.alerts.exceptions.MedicalRecordNotFoundException;
import net.safety.alerts.exceptions.MedicationOrAllergyFormatException;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.service.MedicalRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
public class MedicalRecordController {

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);


    @Autowired
    private MedicalRecordService medicalRecordService;

    @PostMapping(value = "/medicalRecord", produces = {"application/json"}, consumes={"application/json"})
    public ResponseEntity<MedicalRecord> create(@Valid @RequestBody  MedicalRecord medicalRecord) throws MedicationOrAllergyFormatException, JsonProcessingException {
        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();

        if(!medicalRecordService.isMedicationListValid(medicalRecord.getMedications())
                || !medicalRecordService.isAllergyListValid(medicalRecord.getAllergies())){
            logger.error("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg");
            throw new MedicationOrAllergyFormatException("The medication format or allergy format is not valid, medication or allergy should contain only lowercase letters between 2 or 15. Medication should have a dose in mg or ml such as doliparane:500mg");
        }else if(medicalRecordService.doesMedicalRecordExist(medicalRecord)){
            logger.debug("Medical record already exists with firstname {} and lastname {}", medicalRecord.getFirstName(), medicalRecord.getLastName());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.created(currentUri).body(medicalRecordService.save(medicalRecord));
    }

    @PutMapping(value = "/medicalRecord", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<MedicalRecord> update(@Valid @RequestBody MedicalRecord medicalRecord) throws JsonProcessingException, MedicalRecordNotFoundException {

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

    @DeleteMapping("/medicalRecord/{lastname}/{firstname}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("lastname") @NotBlank String lastName, @PathVariable("firstname") @NotBlank String firstName) throws JsonProcessingException {

       if(!medicalRecordService.doesMedicalRecordExistWithFirstNameAndLastName(firstName, lastName)){
            logger.error("Medical record does not exist with firstname {} and lastname {}", firstName, lastName);
            throw new MedicalRecordNotFoundException(String.format("Medical record does not exist with firstname %s and lastname %s", firstName, lastName));
        }
        boolean isRemoved = medicalRecordService.remove(firstName, lastName);
        Map<String, String> response = new HashMap<>();
        response.put("date", new Date().toString());
        response.put("method", "PUT");
        if(isRemoved){
            response.put("status", "200");
            response.put("message", String.format("Medical record with firstname %s and lastname %s has been removed successfully", firstName, lastName));
            return ResponseEntity.ok(response);
        }else{
            response.put("status", "400");
            response.put("message", String.format("Medical record with firstname %s and lastname %s was not removed correctly", firstName, lastName));
            return ResponseEntity.badRequest().body(response);
        }

    }

}
