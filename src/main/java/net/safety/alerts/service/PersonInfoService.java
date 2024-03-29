package net.safety.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.MedicalRecordsDAO;
import net.safety.alerts.dao.PersonsDAO;
import net.safety.alerts.dto.PersonInfoDTO;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.model.Person;
import net.safety.alerts.utils.AlertsUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class PersonInfoService {

    private static final Logger logger = LoggerFactory.getLogger(PersonInfoService.class);

    private List<PersonInfoDTO> personInfoDTOList;

    @Autowired
    private PersonsDAO personsDAO;

    @Autowired
    private MedicalRecordsDAO medicalRecordsDAO;
    public boolean doesPersonExists(String firstName, String lastname) throws JsonProcessingException {
        List<Person>persons = personsDAO.findAll();
        for(Person person :persons){
            if(firstName !=null && !firstName.isEmpty()){
                if(person.getFirstName().equalsIgnoreCase(firstName)
                        && person.getLastName().equalsIgnoreCase(lastname)){
                    logger.debug("Person was found with {} and {}", firstName, lastname);
                    return true;
                }
            }else{
                if(person.getLastName().equalsIgnoreCase(lastname)){
                    logger.debug("Almost one person was found with lastname {}", lastname);
                    return true;
                }
            }
        }
        return false;
    }

    private List<PersonInfoDTO> getSinglePersonWithLastNameAndFirstName(String firstName, String lastName) throws JsonProcessingException {
        personInfoDTOList = new ArrayList<>();
        logger.info("Create a list of person found with lastname : {}",lastName);
        List<Person> personFoundWithLastName = personsDAO.findAll().stream()
                .filter(person -> person.getLastName().equalsIgnoreCase(lastName))
                .toList();
        logger.debug("List of persons found with lastname "+lastName+" : "+personFoundWithLastName);
        List<MedicalRecord>medicalRecords = medicalRecordsDAO.findAll();
        if(firstName != null && !firstName.isEmpty()){
            for(Person person : personFoundWithLastName){
                if(person.getFirstName().equalsIgnoreCase(firstName) && person.getLastName().equalsIgnoreCase(lastName)){
                    for (MedicalRecord medicalRecord : medicalRecords) {
                        if(medicalRecord.getFirstName().equalsIgnoreCase(firstName) && medicalRecord.getLastName().equalsIgnoreCase(lastName)){
                            Map<String, List<String>> medicalInfos = new HashMap<>();
                            medicalInfos.put("medications", medicalRecord.getMedications());
                            medicalInfos.put("allergies", medicalRecord.getAllergies());
                            personInfoDTOList.add(new PersonInfoDTO(person.getFirstName() + " " + person.getLastName(), person.getAddress(),
                                    AlertsUtility.calculateAgeFromDate(medicalRecord.getBirthDate()), person.getEmail(), medicalInfos));
                            return personInfoDTOList;
                        }
                    }
                }
            }
        }
        return personInfoDTOList;
    }
    public List<PersonInfoDTO> getPersonList(String firstName, String lastName) throws JsonProcessingException {
        personInfoDTOList = getSinglePersonWithLastNameAndFirstName(firstName, lastName);
        List<Person> personFoundWithLastName = personsDAO.findAll().stream()
                .filter(person -> person.getLastName().equalsIgnoreCase(lastName))
                .toList();
        List<MedicalRecord>medicalRecords = medicalRecordsDAO.findAll();
        if(personInfoDTOList.isEmpty()){
            logger.info("Create a list of persons having the same lastname {}", lastName);
            for(Person person : personFoundWithLastName){
                for (MedicalRecord medicalRecord : medicalRecords) {
                    if (person.getFirstName().equals(medicalRecord.getFirstName()) && person.getLastName().equals(medicalRecord.getLastName())) {
                        logger.debug("For firstname {} and lastname {}, create person infos object", person.getFirstName(), person.getLastName());
                        Map<String, List<String>> medicalInfos = new HashMap<>();
                        medicalInfos.put("medications", medicalRecord.getMedications());
                        medicalInfos.put("allergies", medicalRecord.getAllergies());
                        personInfoDTOList.add(new PersonInfoDTO(person.getFirstName() + " " + person.getLastName(), person.getAddress(),
                                AlertsUtility.calculateAgeFromDate(medicalRecord.getBirthDate()), person.getEmail(), medicalInfos));
                    }
                }
            }
            logger.debug("List of persons with medical data added : {}", personInfoDTOList);
        }
        return personInfoDTOList;
    }
}
