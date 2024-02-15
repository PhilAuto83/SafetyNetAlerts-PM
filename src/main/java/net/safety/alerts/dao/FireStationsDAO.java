package net.safety.alerts.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.model.AlertsData;
import net.safety.alerts.model.FireStation;


import java.util.List;

public class FireStationsDAO {

    public List<FireStation> getFireStations() throws JsonProcessingException {
        return AlertsDAO.getData().getFireStations();
    }

    public void savePersons(List<FireStation>fireStations) throws JsonProcessingException {
        AlertsDAO.save(new AlertsData(AlertsDAO.getData().getPersons(),fireStations,AlertsDAO.getData().getMedicalRecords()));
    }
}
