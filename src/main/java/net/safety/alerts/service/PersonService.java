package net.safety.alerts.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.dao.PersonsDAO;
import net.safety.alerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

    @Autowired
    private PersonsDAO personsDAO;

    public boolean doesPersonAlreadyExist(Person person) throws JsonProcessingException {
        for(Person personInFile : personsDAO.getPersons()){
            if(personInFile.getFirstName().equalsIgnoreCase(person.getFirstName())&& personInFile.getLastName().equalsIgnoreCase(person.getLastName())){
                return true;
            }
        }
        return false;
    }

    public void save(Person person) throws JsonProcessingException {
        List<Person> persons = personsDAO.getPersons();
        persons.add(person);
        logger.debug("Person : {} {} added to alerts data file", person.getFirstName(), person.getLastName());
        personsDAO.savePersons(persons);
        logger.info("Saving person to file {}", AlertsDAO.getFilePath());
    }
}
