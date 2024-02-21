package net.safety.alerts.service;

import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.model.Person;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PhoneAlertService {

    List<Person> persons = AlertsDAO.getData().getPersons();
    List<FireStation> fireStations = AlertsDAO.getData().getFireStations();

    public Set<String> getPhonesByFireStation(String stationNumber){
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
