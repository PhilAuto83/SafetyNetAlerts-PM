package net.safety.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.FireStationsDAO;
import net.safety.alerts.dao.MedicalRecordsDAO;
import net.safety.alerts.dao.PersonsDAO;
import net.safety.alerts.dto.FireDTO;

import net.safety.alerts.dto.PersonMedicalDataDTO;
import net.safety.alerts.exceptions.StationNumberNotFoundException;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static java.time.temporal.ChronoUnit.YEARS;

@Service
public class FireService {

    private static final Logger logger = LoggerFactory.getLogger(FireService.class);

    @Autowired
    private FireStationsDAO fireStationsDAO;
    @Autowired
    private PersonsDAO personsDAO;
    @Autowired
    private MedicalRecordsDAO medicalRecordsDAO;

    private String getStationNumberByAddress(String address) throws JsonProcessingException {
        List<FireStation>fireStations = fireStationsDAO.getFireStations();
        String stationNumber = null;
        for(FireStation fireStation: fireStations){
            if(fireStation.getAddress().equals(address)){
                stationNumber = fireStation.getStation();
            }
        }
        logger.debug("In getStationNumberByAddress({}), station number retrieved is {}",address, stationNumber);
        return stationNumber;
    }

    private List<PersonMedicalDataDTO> getPersonMedicalDataListFromAddress(String address) throws JsonProcessingException {
        List<PersonMedicalDataDTO> personMedicalDataDTOList = new ArrayList<>();
        List<MedicalRecord> medicalRecords = medicalRecordsDAO.getMedicalRecords();
        logger.debug("Size of medicalRecords is : {}", medicalRecords.size());
        List<Person> persons = personsDAO.getPersons();
        logger.debug("Size of person list is : {}", persons.size());
        for(Person person : persons){
            if(person.getAddress().equals(address)){
                for(MedicalRecord medicalRecord : medicalRecords){
                    if(medicalRecord.getFirstName().equals(person.getFirstName())
                            && medicalRecord.getLastName().equals(person.getLastName())){
                        int age = (int)YEARS.between(medicalRecord.getBirthDate(), LocalDate.now());
                        Map<String, List<String>> medicalInfos = new HashMap<>();
                        medicalInfos.put("medications", medicalRecord.getMedications());
                        medicalInfos.put("allergies", medicalRecord.getAllergies());
                        personMedicalDataDTOList.add(new PersonMedicalDataDTO(person.getFirstName(), person.getLastName(),
                        person.getPhone(), age, medicalInfos));
                    }
                }
            }
        }
        logger.debug("List of person with medical info is not empty : {}", personMedicalDataDTOList);
        return personMedicalDataDTOList;
    }

    public FireDTO getPersonMedicalInfoByAddress(String address) throws JsonProcessingException {

        if(getStationNumberByAddress(address)==null){
            logger.error("No station was retrieved when searching by address for address {}", address);
            throw new StationNumberNotFoundException("Station number does not exist at address : "+address);
        }

        return new FireDTO(getPersonMedicalDataListFromAddress(address), getStationNumberByAddress(address));
    }
}