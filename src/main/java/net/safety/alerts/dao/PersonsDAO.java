package net.safety.alerts.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.model.AlertsData;
import net.safety.alerts.model.Person;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class PersonsDAO {


    public List<Person> getPersons() throws JsonProcessingException {
        return AlertsDAO.getData().getPersons();
    }

    public void savePersons(List<Person>persons){
        AlertsDAO.save(new AlertsData(persons,AlertsDAO.getData().getFireStations(),AlertsDAO.getData().getMedicalRecords()));
    }
}
