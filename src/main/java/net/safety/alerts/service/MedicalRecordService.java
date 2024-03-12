package net.safety.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.MedicalRecordsDAO;
import net.safety.alerts.model.MedicalRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class MedicalRecordService {

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordService.class);

    @Autowired
    private MedicalRecordsDAO medicalRecordsDAO;

    public boolean isMedicationListValid(List<String> medications){
        if(medications==null || medications.isEmpty() || (medications.contains("")&& medications.size()==1)){
            return true;
        }
        for(String medication : medications){
            Pattern pattern = Pattern.compile("^[a-z]{2,15}:[0-9]{1,4}m[gl]$");
            if(!pattern.matcher(medication).find()){
                return false;
            }
        }
        return true;
    }

    public boolean isAllergyListValid(List<String> allergies){
        if(allergies==null || allergies.isEmpty() || (allergies.size()==1 && allergies.contains(""))){
            return true;
        }
        for(String allergy : allergies){
            Pattern pattern = Pattern.compile("^[a-z]{2,15}$");
            if(!pattern.matcher(allergy).find()){
                return false;
            }
        }
        return true;
    }

    public boolean doesMedicalRecordExist(MedicalRecord medicalRecord) throws JsonProcessingException {
        List<MedicalRecord> medicalRecords = medicalRecordsDAO.findAll();
        for (MedicalRecord medicalRecordInFile : medicalRecords) {
            if(medicalRecordInFile.getFirstName().equals(medicalRecord.getFirstName())
                    && medicalRecordInFile.getLastName().equals(medicalRecord.getLastName())){
                return true;
            }
        }
        return false;
    }

    public boolean doesMedicalRecordExistWithFirstNameAndLastName(String firstName, String lastName) throws JsonProcessingException {
        List<MedicalRecord> medicalRecords = medicalRecordsDAO.findAll();
        for (MedicalRecord medicalRecordInFile : medicalRecords) {
            if(medicalRecordInFile.getFirstName().equalsIgnoreCase(firstName)
                    && medicalRecordInFile.getLastName().equalsIgnoreCase(lastName)){
                return true;
            }
        }
        return false;
    }

    public MedicalRecord save(MedicalRecord medicalRecord) throws JsonProcessingException {
        List<MedicalRecord> medicalRecords = medicalRecordsDAO.findAll();
        medicalRecordsDAO.save(medicalRecord);
        logger.debug("Medical record {} has been saved to file", medicalRecord);
        return medicalRecordsDAO.findAll().getLast();
    }

    public MedicalRecord update(MedicalRecord medicalRecord) throws JsonProcessingException {
        List<MedicalRecord> medicalRecords = medicalRecordsDAO.findAll();
        medicalRecords.removeIf(medicalRecordInFile ->{
            return medicalRecordInFile.getFirstName().equals(medicalRecord.getFirstName())
                    && medicalRecordInFile.getLastName().equals(medicalRecord.getLastName());
        });
        medicalRecords.add(medicalRecord);
        medicalRecordsDAO.save(medicalRecord);
        logger.debug("Medical record {} has been updated to file", medicalRecord);
        return medicalRecordsDAO.findAll().getLast();
    }

    public boolean remove(String firstName, String lastName) {
        List<MedicalRecord> medicalRecords = medicalRecordsDAO.findAll();
        medicalRecords.removeIf(medicalRecordInFile ->{
            return medicalRecordInFile.getFirstName().equalsIgnoreCase(firstName)
                    && medicalRecordInFile.getLastName().equalsIgnoreCase(lastName);
        });
        for (MedicalRecord medicalRecord : medicalRecords) {
            if(medicalRecord.getFirstName().equalsIgnoreCase(firstName)
                    && medicalRecord.getLastName().equalsIgnoreCase(lastName)){
                logger.debug("Medical record with firstname {} and lastname {} was not really removed", firstName, lastName);
                return false;
            }
        }
        return true;
    }
}
