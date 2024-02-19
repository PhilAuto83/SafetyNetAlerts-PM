package net.safety.alerts.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.model.AlertsData;
import net.safety.alerts.model.FireStation;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class FireStationsDAO extends AlertsDAO{

    public List<FireStation> getFireStations() throws JsonProcessingException {
        return getData().getFireStations();
    }

    public void savePersons(List<FireStation>fireStations, String filePath) throws JsonProcessingException {
        AlertsDAO.save(new AlertsData(getData().getPersons(),fireStations,getData().getMedicalRecords()));
    }
}
