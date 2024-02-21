package net.safety.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.dao.FireStationsDAO;
import net.safety.alerts.dao.MedicalRecordsDAO;
import net.safety.alerts.dao.PersonsDAO;
import net.safety.alerts.dto.PersonByFireStation;
import net.safety.alerts.dto.PersonDTO;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.YEARS;

@Service
public class FireStationService {

    private static final Logger logger = LoggerFactory.getLogger(FireStationService.class);

    @Autowired
    private FireStationsDAO fireStationsDAO;
    @Autowired
    private PersonsDAO personsDAO;
    @Autowired
    private MedicalRecordsDAO medicalRecordsDAO;

    public boolean doesStationNumberExist(String stationNumber) throws JsonProcessingException {
        List<FireStation> fireStationList = fireStationsDAO.getFireStations();
        for(FireStation firestation : fireStationList){
            if(firestation.getStation().equals(String.valueOf(stationNumber))){
                return true;
            }
        }
        return false;
    }

    private List<PersonDTO> getRestrictedPersonInfoByStationNumber(String stationNumber) throws JsonProcessingException {
        List<PersonDTO> personsRestrictedInfo = new ArrayList<>();
        List<FireStation> fireStationList = fireStationsDAO.getFireStations();
        List<Person>personList= personsDAO.getPersons();
        for(FireStation firestation : fireStationList){
            if(firestation.getStation().equals(stationNumber)){
                for(Person person : personList){
                    if(person.getAddress().equals(firestation.getAddress())){
                        personsRestrictedInfo.add(new PersonDTO(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone()));
                    }
                }
            }
        }
        return personsRestrictedInfo;
    }

    private int getNumberOfAdults(String stationNumber) throws JsonProcessingException {
        int nbAdults = 0;
        List<PersonDTO> personRestrictedInfoList = getRestrictedPersonInfoByStationNumber(stationNumber);
        List<MedicalRecord>medicalRecordList = medicalRecordsDAO.getMedicalRecords();
        for (PersonDTO person : personRestrictedInfoList) {
            for (MedicalRecord medicalRecord : medicalRecordList) {
                if (person.getFirstName().equals(medicalRecord.getFirstName()) && person.getLastName().equals(medicalRecord.getLastName())) {
                    if (YEARS.between(medicalRecord.getBirthDate(),LocalDate.now()) > 18) {
                        nbAdults++;
                    }
                }
            }
        }
        return nbAdults;
    }

    private int getNumberOfChildren(String stationNumber) throws JsonProcessingException {
        int nbChildren = 0;
        List<PersonDTO> personRestrictedInfoList = getRestrictedPersonInfoByStationNumber(stationNumber);
        List<MedicalRecord>medicalRecordList = medicalRecordsDAO.getMedicalRecords();
        for (PersonDTO person : personRestrictedInfoList) {
            for (MedicalRecord medicalRecord : medicalRecordList) {
                if (person.getFirstName().equals(medicalRecord.getFirstName()) && person.getLastName().equals(medicalRecord.getLastName())) {
                    if (YEARS.between(medicalRecord.getBirthDate(),LocalDate.now()) <= 18) {
                        nbChildren++;
                    }
                }
            }
        }
        return nbChildren;
    }

    public PersonByFireStation getPersonsInfoByStationNumber(String stationNumber) throws JsonProcessingException {
        return  new PersonByFireStation(getRestrictedPersonInfoByStationNumber(stationNumber),getNumberOfAdults(stationNumber), getNumberOfChildren(stationNumber));
    }

}
