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

    public Person save(Person person) throws JsonProcessingException {
        List<Person> persons = personsDAO.getPersons();
        persons.add(person);
        logger.debug("Person : {} {} added to alerts data file", person.getFirstName(), person.getLastName());
        personsDAO.savePersons(persons);
        logger.info("Saving person to file {}", AlertsDAO.getFilePath());
        logger.info("Return last person saved in person's list");
        return personsDAO.getPersons().getLast();
    }

    public Person update(Person person) throws JsonProcessingException {
        List<Person> persons = personsDAO.getPersons();
        persons.removeIf(personInFile -> {
             return personInFile.getFirstName().equalsIgnoreCase(person.getFirstName())
                    && personInFile.getLastName().equalsIgnoreCase(person.getLastName());
        });
        persons.add(person);
        logger.debug("Person : {} {} updated to alerts data file", person.getFirstName(), person.getLastName());
        personsDAO.savePersons(persons);
        logger.info("Updating person {} {} in file {}", person.getFirstName(), person.getLastName(), AlertsDAO.getFilePath());
        return personsDAO.getPersons().getLast();
    }

    public void remove(String firstName, String lastName) throws JsonProcessingException {

        List<Person> persons = personsDAO.getPersons();
        logger.debug("Try to remove person : {} {} from alerts data file", firstName, lastName);
        persons.removeIf(personInFile -> {
            return personInFile.getFirstName().equalsIgnoreCase(firstName)
                    && personInFile.getLastName().equalsIgnoreCase(lastName);
        });
        personsDAO.savePersons(persons);
        for(Person personInFile : persons){
            if(personInFile.getFirstName().equalsIgnoreCase(firstName)&&
                    personInFile.getLastName().equalsIgnoreCase(lastName)){
                logger.info("Person was not removed from list");
            }
        }
    }
}
