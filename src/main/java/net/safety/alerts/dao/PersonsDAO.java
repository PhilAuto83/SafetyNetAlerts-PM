package net.safety.alerts.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.model.AlertsData;
import net.safety.alerts.model.Person;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class PersonsDAO extends AlertsDAO {


    public List<Person> getPersons() throws JsonProcessingException {
        return getData().getPersons();
    }

    public void savePersons(List<Person>persons){
        AlertsDAO.save(new AlertsData(persons,getData().getFireStations(),getData().getMedicalRecords()));
    }
}
