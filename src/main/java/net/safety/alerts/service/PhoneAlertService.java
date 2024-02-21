package net.safety.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.dao.FireStationsDAO;
import net.safety.alerts.dao.MedicalRecordsDAO;
import net.safety.alerts.dao.PersonsDAO;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PhoneAlertService {

    @Autowired
    private FireStationsDAO fireStationsDAO;
    @Autowired
    private PersonsDAO personsDAO;


    public Set<String> getPhonesByFireStation(String stationNumber) throws JsonProcessingException {
        List<Person>persons = personsDAO.getPersons();
        List<FireStation>fireStations = fireStationsDAO.getFireStations();
        Set<String> phones = new HashSet<>();
        for(FireStation fireStation : fireStations){
            if(fireStation.getStation().equals(stationNumber)){
                for(Person person : persons){
                    if(fireStation.getAddress().equals(person.getAddress())){
                        phones.add(person.getPhone());
                    }
                }
            }

        }
        return phones;
    }
}
