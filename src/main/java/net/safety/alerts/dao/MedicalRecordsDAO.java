package net.safety.alerts.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.model.AlertsData;
import net.safety.alerts.model.MedicalRecord;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class MedicalRecordsDAO extends AlertsDAO{

    public List<MedicalRecord> getFireStations()  {
        return AlertsDAO.getData().getMedicalRecords();
    }

    public void savePersons(List<MedicalRecord>medicalRecords, String filename) throws JsonProcessingException {
        AlertsDAO.save(new AlertsData(getData().getPersons(),getData().getFireStations(),medicalRecords));
    }
}
