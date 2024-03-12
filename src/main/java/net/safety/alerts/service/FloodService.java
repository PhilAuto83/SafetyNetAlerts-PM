package net.safety.alerts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.FireStationsDAO;
import net.safety.alerts.dao.MedicalRecordsDAO;
import net.safety.alerts.dao.PersonsDAO;
import net.safety.alerts.dto.FloodDTO;
import net.safety.alerts.dto.PersonMedicalDataDTO;
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
public class FloodService {

    private static final Logger logger = LoggerFactory.getLogger(FloodService.class);

    @Autowired
    private FireStationsDAO fireStationsDAO;
    @Autowired
    private PersonsDAO personsDAO;
    @Autowired
    private MedicalRecordsDAO medicalRecordsDAO;


    private List<String> getAddressesFromStationNumber(String stationNumber) throws JsonProcessingException {
        return fireStationsDAO.findAll().stream()
                .filter(fireStation -> fireStation.getStation().equals(stationNumber))
                .map(FireStation::getAddress)
                .toList();
    }

    public List<FloodDTO> getPersonsWithMedicalDataFromStationNumber(String stationNumber) throws JsonProcessingException {
        List<FloodDTO> personsGroupedByAddress = new ArrayList<>();
        logger.info("Get the list of addresses from station number {}", stationNumber);
        List<String> addresses = getAddressesFromStationNumber(stationNumber);
        List<MedicalRecord>medicalRecords = medicalRecordsDAO.findAll();
        List<Person> persons = personsDAO.findAll();
        for(String address : addresses){
            logger.info("Get list of person covered by address {}", address);
            List<Person> personCoveredByAddress = persons.stream().filter(person -> person.getAddress().equals(address)).toList();
            logger.debug("List of person covered by address {} : {}", address, personCoveredByAddress);
            logger.info("Map list of persons to their medical data");
            List<PersonMedicalDataDTO>personsWithMedicalData = personCoveredByAddress.stream().map(person -> {
                return new PersonMedicalDataDTO(person.getFirstName()+" "+person.getLastName(),person.getPhone(),
                        AlertsUtility.getPersonAgeFromMedicalRecords(medicalRecords, person) , AlertsUtility.getPersonMedicalData(medicalRecords, person));
            }).toList();
            logger.info("Add a list of persons covered by address {} to the list of flood dto", address);
            personsGroupedByAddress.add(new FloodDTO(address, personsWithMedicalData));
        }
        return  personsGroupedByAddress;
    }
}
