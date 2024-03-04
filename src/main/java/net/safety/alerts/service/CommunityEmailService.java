package net.safety.alerts.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.PersonsDAO;
import net.safety.alerts.exceptions.CityNotFoundException;
import net.safety.alerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommunityEmailService {

    private static Logger logger = LoggerFactory.getLogger(CommunityEmailService.class);

    @Autowired
    private PersonsDAO personsDAO;

    public List<String> getEmailsFromCity(String city) throws JsonProcessingException {
        List<String> emails = new ArrayList<>();
        List<Person> persons  = personsDAO.getPersons();
        for(Person person : persons){
            if(person.getCity().equalsIgnoreCase(city)){
                emails.add(person.getEmail());
            }
        }
        if(emails.isEmpty()){
            logger.error("City name {} not found in the list of person's address.", city);
            throw new CityNotFoundException(String.format("City name %s not found in the list of person's address.", city));
        }
        logger.debug(String.format("Emails retrieved from city %s : ", emails));
        return emails;
    }
}
