package net.safety.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.dto.PersonByFireStation;
import net.safety.alerts.dto.PersonDTO;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.MONTHS;

@Service
public class FireStationService {

    private static final Logger logger = LoggerFactory.getLogger(FireStationService.class);

    List<Person>personList;
    List<FireStation>fireStationList;
    List<MedicalRecord> medicalRecordList;

    public boolean doesStationNumberExist(String stationNumber){
        fireStationList = AlertsDAO.getData().getFireStations();
        for(FireStation firestation : fireStationList){
            if(firestation.getStation().equals(String.valueOf(stationNumber))){
                return true;
            }
        }
        return false;
    }

    private List<PersonDTO> getRestrictedPersonInfoByStationNumber(String stationNumber) throws JsonProcessingException {
        List<PersonDTO> personsRestrictedInfo = new ArrayList<>();
        fireStationList = AlertsDAO.getData().getFireStations();
        personList= AlertsDAO.getData().getPersons();
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
        medicalRecordList = AlertsDAO.getData().getMedicalRecords();
        for (PersonDTO person : personRestrictedInfoList) {
            for (MedicalRecord medicalRecord : medicalRecordList) {
                if (person.getFirstName().equals(medicalRecord.getFirstName()) && person.getLastName().equals(medicalRecord.getLastName())) {
                    if (MONTHS.between(medicalRecord.getBirthDate(),LocalDate.now()) > 192) {
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
        medicalRecordList = AlertsDAO.getData().getMedicalRecords();
        for (PersonDTO person : personRestrictedInfoList) {
            for (MedicalRecord medicalRecord : medicalRecordList) {
                if (person.getFirstName().equals(medicalRecord.getFirstName()) && person.getLastName().equals(medicalRecord.getLastName())) {
                    if (MONTHS.between(medicalRecord.getBirthDate(),LocalDate.now()) < 192) {
                        nbChildren++;
                    }
                }
            }
        }
        return nbChildren;
    }

    public PersonByFireStation getPersonsInfoByStationNumber(String stationNumber) throws JsonProcessingException {
        if(getRestrictedPersonInfoByStationNumber(stationNumber).equals(new ArrayList<PersonDTO>())){
            return null;
        }
        return  new PersonByFireStation(getRestrictedPersonInfoByStationNumber(stationNumber),getNumberOfAdults(stationNumber), getNumberOfChildren(stationNumber));
    }



}
