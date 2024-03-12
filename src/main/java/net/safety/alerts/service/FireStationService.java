package net.safety.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.FireStationsDAO;
import net.safety.alerts.dao.MedicalRecordsDAO;
import net.safety.alerts.dao.PersonsDAO;
import net.safety.alerts.dto.PersonByFireStationDTO;
import net.safety.alerts.dto.PersonDTO;
import net.safety.alerts.exceptions.StationNumberNotFoundException;
import net.safety.alerts.model.FireStation;
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
public class FireStationService {

    private static final Logger logger = LoggerFactory.getLogger(FireStationService.class);

    @Autowired
    private FireStationsDAO fireStationsDAO;
    @Autowired
    private PersonsDAO personsDAO;
    @Autowired
    private MedicalRecordsDAO medicalRecordsDAO;

    public boolean doesStationNumberExist(String stationNumber) throws JsonProcessingException {
        List<FireStation> fireStationList = fireStationsDAO.findAll();
        for(FireStation firestation : fireStationList){
            if(firestation.getStation().equals(stationNumber)){
                return true;
            }
        }
        logger.debug("No station number {} exists in the list of firestations.", stationNumber );
        return false;
    }

    private List<PersonDTO> getRestrictedPersonInfoByStationNumber(String stationNumber) throws JsonProcessingException {
        List<PersonDTO> personsRestrictedInfo = new ArrayList<>();
        List<FireStation> fireStations = fireStationsDAO.findAll();
        List<Person>persons= personsDAO.findAll();
        List<Person> personsCoveredByStationNumber = AlertsUtility.getListOfPersonFromFireStationNumber(persons, fireStations, stationNumber);
        for(Person person : personsCoveredByStationNumber){
           personsRestrictedInfo.add(new PersonDTO(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone()));
        }
        return personsRestrictedInfo;
    }

    private int getNumberOfAdults(String stationNumber) throws JsonProcessingException {
        int nbAdults = 0;
        List<PersonDTO> personRestrictedInfoList = getRestrictedPersonInfoByStationNumber(stationNumber);
        List<MedicalRecord>medicalRecordList = medicalRecordsDAO.findAll();
        for (PersonDTO person : personRestrictedInfoList) {
            for (MedicalRecord medicalRecord : medicalRecordList) {
                if (person.getFirstName().equals(medicalRecord.getFirstName()) && person.getLastName().equals(medicalRecord.getLastName())) {
                    if (AlertsUtility.calculateAgeFromDate(medicalRecord.getBirthDate()) > 18) {
                        nbAdults++;
                    }
                }
            }
        }
        logger.debug("Number of adults retrieved from method getNumberOfAdults({}) is {}", stationNumber, nbAdults);
        return nbAdults;
    }

    private int getNumberOfChildren(String stationNumber) throws JsonProcessingException {
        int nbChildren = 0;
        List<PersonDTO> personRestrictedInfoList = getRestrictedPersonInfoByStationNumber(stationNumber);
        List<MedicalRecord>medicalRecordList = medicalRecordsDAO.findAll();
        for (PersonDTO person : personRestrictedInfoList) {
            for (MedicalRecord medicalRecord : medicalRecordList) {
                if (person.getFirstName().equals(medicalRecord.getFirstName()) && person.getLastName().equals(medicalRecord.getLastName())) {
                    if (AlertsUtility.calculateAgeFromDate(medicalRecord.getBirthDate()) <= 18) {
                        nbChildren++;
                    }
                }
            }
        }
        logger.debug("Number of children retrieved from method getNumberOfChildren({}) is {}", stationNumber,  nbChildren);
        return nbChildren;
    }

    public PersonByFireStationDTO getPersonsInfoByStationNumber(String stationNumber) throws JsonProcessingException {

        return  new PersonByFireStationDTO(getRestrictedPersonInfoByStationNumber(stationNumber),getNumberOfAdults(stationNumber), getNumberOfChildren(stationNumber));
    }

    public boolean doesStationAlreadyExist(String address, String number) throws JsonProcessingException {
        boolean isFound = false;
        for(FireStation fireStationInFile : fireStationsDAO.findAll()){
            if (fireStationInFile.getStation().equalsIgnoreCase(number)
                    && fireStationInFile.getAddress().equalsIgnoreCase(address.trim()))
             {
                return true;
            }
        }
        return isFound;
    }
    public boolean doesNumberOrAddressExists(String numberOrAddress) throws JsonProcessingException {
        boolean isFound = false;
        for(FireStation fireStationInFile : fireStationsDAO.findAll()){
            if (fireStationInFile.getStation().equalsIgnoreCase(numberOrAddress)
                    || fireStationInFile.getAddress().equalsIgnoreCase(numberOrAddress))
            {
                return true;
            }
        }
        return isFound;
    }

    public FireStation save(FireStation fireStation) throws JsonProcessingException {
        List<FireStation> fireStations = fireStationsDAO.findAll();
        fireStationsDAO.save(fireStation);
        return fireStationsDAO.findAll().getLast();
    }

    public void remove(String numberOrAddress) throws JsonProcessingException {
        List<FireStation> fireStations = fireStationsDAO.findAll();
        logger.debug("Try to remove station with number or address {} from alerts data file", numberOrAddress);
        fireStations.removeIf(stationInFile -> {
            return (stationInFile.getStation().equalsIgnoreCase(numberOrAddress.trim())
                    || stationInFile.getAddress().equalsIgnoreCase(numberOrAddress.trim()));
        });
        for(FireStation stationInFile : fireStationsDAO.findAll()){
            if(stationInFile.getStation().equalsIgnoreCase(numberOrAddress.trim())
                    || stationInFile.getAddress().equalsIgnoreCase(numberOrAddress.trim())){
                logger.info("Station was not removed from list");
            }
        }
    }

    public FireStation update(FireStation newStation) throws JsonProcessingException {
        List<FireStation> fireStations = fireStationsDAO.findAll();
        logger.debug("Try to update station with address {} from alerts data file", newStation.getAddress());
        fireStations.removeIf(stationInFile -> {
            return stationInFile.getAddress().equalsIgnoreCase(newStation.getAddress());
        });
        fireStationsDAO.save(newStation);
        for(FireStation stationInFile : fireStationsDAO.findAll()){
            if(stationInFile.getAddress().equalsIgnoreCase(newStation.getAddress())){
                logger.info("Station was not removed from list");
            }
        }
        return fireStationsDAO.findAll().getLast();

    }
}
