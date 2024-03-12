package net.safety.alerts.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.model.AlertsData;
import net.safety.alerts.model.MedicalRecord;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class MedicalRecordsDAO extends AlertsDAO{

    List<MedicalRecord>medicalRecordsInFile = getData().getMedicalRecords();
    List<MedicalRecord>medicalRecordsInMemory = medicalRecordsInFile;

    public List<MedicalRecord>findAll(){
        return medicalRecordsInMemory;
    }

    public void save(MedicalRecord medicalRecord){
        medicalRecordsInMemory.add(medicalRecord);
    }

    public  void delete(MedicalRecord medicalRecord){
        medicalRecordsInMemory.removeIf(medicalRecordInFile -> {
            return (medicalRecordInFile.getFirstName().equals(medicalRecord.getFirstName())
                    && medicalRecordInFile.getLastName().equals(medicalRecord.getLastName()));
        });
    }
}
