package net.safety.alerts.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.MedicalRecordsDAO;
import net.safety.alerts.dao.PersonsDAO;
import net.safety.alerts.dto.ChildAlertDTO;
import net.safety.alerts.dto.PersonAgeDTO;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.model.Person;
import net.safety.alerts.utils.AlertsUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;



@Service
public class ChildAlertService {
    private static final Logger logger = LoggerFactory.getLogger(ChildAlertService.class);


    @Autowired
    private PersonsDAO personsDAO;
    @Autowired
    private MedicalRecordsDAO medicalRecordsDAO;

    public int getAddressOccurrence(String address) throws JsonProcessingException {
        List<Person> personList = personsDAO.getPersons();
        int countAddressOccurrence = 0;
        for (Person person : personList) {
            if (person.getAddress().equals(address)) {
                countAddressOccurrence++;
            }
        }
        logger.debug("When calling getAddressOccurrence, count for address {} is {}.", address, countAddressOccurrence);
        return countAddressOccurrence;
    }

    public ChildAlertDTO getPersonFromAddress(String address) throws JsonProcessingException {
        List<Person> personsLivingAtAddress = AlertsUtility.getListOfPersonFromAddress(personsDAO.getPersons(), address);
        List<MedicalRecord> medicalRecordList = medicalRecordsDAO.getMedicalRecords();
        List<PersonAgeDTO> childrenList = new ArrayList<>();
        List<PersonAgeDTO> otherMembers = new ArrayList<>();

        for (Person person : personsLivingAtAddress) {
            for (MedicalRecord medicalRecord : medicalRecordList) {
                if (medicalRecord.getFirstName().equals(person.getFirstName()) && medicalRecord.getLastName().equals(person.getLastName())) {
                    int age = AlertsUtility.calculateAgeFromDate(medicalRecord.getBirthDate());
                    if (age <= 18) {
                        childrenList.add(new PersonAgeDTO(person.getFirstName(), person.getLastName(), age));
                    }
                    otherMembers.add(new PersonAgeDTO(person.getFirstName(), person.getLastName(), age));
                }
            }
        }
        logger.debug("Children list retrieved from address {} has size {}", address, childrenList.size());
        logger.debug("Adult list retrieved from address {} has size {}", address, otherMembers.size());
        return new ChildAlertDTO(childrenList, otherMembers);
    }
}


