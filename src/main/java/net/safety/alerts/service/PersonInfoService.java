package net.safety.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.MedicalRecordsDAO;
import net.safety.alerts.dao.PersonsDAO;
import net.safety.alerts.dto.PersonInfoDTO;
import net.safety.alerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;




@Service
public class PersonInfoService {

    private static final Logger logger = LoggerFactory.getLogger(PersonInfoService.class);

    @Autowired
    private PersonsDAO personsDAO;

    @Autowired
    private MedicalRecordsDAO medicalRecordsDAO;
    public boolean doesPersonExists(String firstName, String lastname) throws JsonProcessingException {
        List<Person>persons = personsDAO.getPersons();
        for(Person person :persons){
            if(firstName !=null && !firstName.isEmpty()){
                if(person.getFirstName().equals(firstName)&&person.getLastName().equals(lastname)){
                    return true;
                }
            }else{
                if(person.getLastName().equals(lastname)){
                    return true;
                }
            }
        }
        return false;
    }
    public List<PersonInfoDTO> getPersonList(String firstName, String lastName) {
        return null;
    }
}
