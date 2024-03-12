package net.safety.alerts.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.model.FireStation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FireStationsDAO extends AlertsDAO{

    List<FireStation>fireStationsInFile = getData().getFireStations();
    List<FireStation>fireStationsInMemory = fireStationsInFile;


    public List<FireStation>findAll(){
        return fireStationsInMemory;
    }

    public void save(FireStation station) throws JsonProcessingException {
        fireStationsInMemory.add(station);
    }

    public void delete(String numberOrAddress) throws JsonProcessingException {
        fireStationsInMemory.removeIf(fireStation -> fireStation.getStation().equals(numberOrAddress)|| fireStation.getAddress().equalsIgnoreCase(numberOrAddress));
    }
}
