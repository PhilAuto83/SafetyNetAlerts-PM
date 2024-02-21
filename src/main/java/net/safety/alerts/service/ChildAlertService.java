package net.safety.alerts.service;


import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.dto.ChildAlertDTO;
import net.safety.alerts.dto.PersonAgeDTO;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.YEARS;

@Service
public class ChildAlertService {
    private static final Logger logger = LoggerFactory.getLogger(ChildAlertService.class);

    List<Person> personList = AlertsDAO.getData().getPersons();
    List<MedicalRecord> medicalRecordList = AlertsDAO.getData().getMedicalRecords();

    public int getAddressOccurrence(String address){
        int countAddressOccurrence = 0;
        for(Person person : personList){
            if(person.getAddress().equals(address)){
                countAddressOccurrence++;
            }
        }
        return countAddressOccurrence;
    }

    public ChildAlertDTO getPersonFromAddress(String address){
        List<PersonAgeDTO> childrenList = new ArrayList<>();
        List<PersonAgeDTO> otherMembers = new ArrayList<>();

        for(Person person : personList){
            if(person.getAddress().equals(address)){
                for(MedicalRecord medicalRecord: medicalRecordList){
                    if(medicalRecord.getFirstName().equals(person.getFirstName())&&medicalRecord.getLastName().equals(person.getLastName())){
                        int personAge =(int)YEARS.between(medicalRecord.getBirthDate(), LocalDate.now());
                        if(personAge <=18){
                            childrenList.add(new PersonAgeDTO(person.getFirstName(), person.getLastName(),personAge));
                        }else{
                            otherMembers.add(new PersonAgeDTO(person.getFirstName(), person.getLastName(),personAge));
                        }
                    }
                }
            }
        }
        return new ChildAlertDTO(childrenList, otherMembers);
    }
}
