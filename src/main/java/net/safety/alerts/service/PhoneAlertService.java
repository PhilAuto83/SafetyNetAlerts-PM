package net.safety.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.FireStationsDAO;
import net.safety.alerts.dao.PersonsDAO;
import net.safety.alerts.exceptions.PhoneNotFoundException;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PhoneAlertService {

    private static final Logger logger = LoggerFactory.getLogger(PhoneAlertService.class);

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
        if(phones.isEmpty()){
            logger.error("No Phone found for station number {}", stationNumber);
            throw new PhoneNotFoundException(String.format("No Phone found for station number %s", stationNumber));
        }
        logger.debug("Method getPhonesByFireStation({}) returns a list of phones : {}", stationNumber, phones);
        return phones;
    }
}
