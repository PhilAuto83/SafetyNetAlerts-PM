package net.safety.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.MedicalRecordsDAO;
import net.safety.alerts.model.MedicalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class MedicalRecordService {

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
        List<MedicalRecord> medicalRecords = medicalRecordsDAO.getMedicalRecords();
        for (MedicalRecord medicalRecordInFile : medicalRecords) {
            if(medicalRecordInFile.getFirstName().equals(medicalRecord.getFirstName())
                    && medicalRecordInFile.getLastName().equals(medicalRecord.getLastName())){
                return true;
            }
        }
        return false;
    }

    public MedicalRecord save(MedicalRecord medicalRecord) throws JsonProcessingException {
        List<MedicalRecord> medicalRecords = medicalRecordsDAO.getMedicalRecords();
        medicalRecords.add(medicalRecord);
        medicalRecordsDAO.saveRecords(medicalRecords);
        return medicalRecordsDAO.getMedicalRecords().getLast();
    }
}
