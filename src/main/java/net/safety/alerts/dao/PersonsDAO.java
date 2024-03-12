package net.safety.alerts.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.model.AlertsData;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.model.Person;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class PersonsDAO extends AlertsDAO {


    List<Person>personsInFile = getData().getPersons();

    List<Person>personsInMemory = personsInFile;


    public List<Person>findAll(){
        return personsInFile;
    }

    public void save(Person person) throws JsonProcessingException {
        personsInFile.add(person);
    }

    public void delete(String firstName, String lastName) throws JsonProcessingException {
        personsInFile.removeIf(personinDb -> {
            return (personinDb.getFirstName().equalsIgnoreCase(firstName)&&personinDb.getLastName().equalsIgnoreCase(lastName));
        });
    }
}
