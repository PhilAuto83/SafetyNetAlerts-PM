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
        for(Person personInFile : personsDAO.findAll()){
            if(personInFile.getFirstName().equalsIgnoreCase(person.getFirstName())&& personInFile.getLastName().equalsIgnoreCase(person.getLastName())){
                return true;
            }
        }
        return false;
    }

    public boolean areFirstNameAndLastnamePresent(String firstName, String lastName) throws JsonProcessingException {
        for(Person personInFile : personsDAO.findAll()){
            if(personInFile.getFirstName().equalsIgnoreCase(firstName)&& personInFile.getLastName().equalsIgnoreCase(lastName)){
                return true;
            }
        }
        return false;
    }

    public Person save(Person person) throws JsonProcessingException {
        List<Person> persons = personsDAO.findAll();

        logger.debug("Person : {} {} added to alerts data file", person.getFirstName(), person.getLastName());
        personsDAO.save(person);
        logger.info("Saving person to file {}", AlertsDAO.getFilePath());
        logger.info("Return last person saved in person's list");
        return personsDAO.findAll().getLast();
    }

    public Person update(Person person) throws JsonProcessingException {
        List<Person> persons = personsDAO.findAll();
        persons.removeIf(personInFile -> {
             return personInFile.getFirstName().equalsIgnoreCase(person.getFirstName())
                    && personInFile.getLastName().equalsIgnoreCase(person.getLastName());
        });
        logger.debug("Person : {} {} updated to alerts data file", person.getFirstName(), person.getLastName());
        personsDAO.save(person);
        logger.info("Updating person {} {} in file {}", person.getFirstName(), person.getLastName(), AlertsDAO.getFilePath());
        return personsDAO.findAll().getLast();
    }

    public void remove(String firstName, String lastName) throws JsonProcessingException {

        List<Person> persons = personsDAO.findAll();
        logger.debug("Try to remove person : {} {} from alerts data file", firstName, lastName);
        persons.removeIf(personInFile -> {
            return personInFile.getFirstName().equalsIgnoreCase(firstName)
                    && personInFile.getLastName().equalsIgnoreCase(lastName);
        });
        for(Person personInFile : persons){
            if(personInFile.getFirstName().equalsIgnoreCase(firstName)&&
                    personInFile.getLastName().equalsIgnoreCase(lastName)){
                logger.info("Person was not removed from list");
            }
        }
    }
}
