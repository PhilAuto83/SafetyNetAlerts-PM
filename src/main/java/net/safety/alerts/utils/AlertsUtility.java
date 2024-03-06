package net.safety.alerts.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.safety.alerts.dao.MedicalRecordsDAO;
import net.safety.alerts.exceptions.PersonNotFoundException;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.model.Person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.YEARS;

/**
 * This class is used to perform actions repeatable in various classes in service package
 */
public final class AlertsUtility {

    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * This method is computing age from a LocalDate passed as an argument and convert it to int
     * @param birthDate which is  a person's birthDate of type LocalDate
     * @return an int which is the age of the person
     */
    public static int calculateAgeFromDate(LocalDate birthDate){
        return (int)YEARS.between(birthDate, LocalDate.now());
    }

    /**
     * This method helps getting a list of person covered by a station number
     * @param persons which is a list of persons
     * @param fireStations which is a list of firestations
     * @param stationNumber which a number representing a firestation
     * @return a list of persons covered by a firestation number
     */
    public static List<Person> getListOfPersonFromFireStationNumber(List<Person>persons, List<FireStation>fireStations, String stationNumber){
        List<Person> personsCoveredByFireStationAddress = new ArrayList<>();
        for(FireStation fireStation: fireStations){
            if(fireStation.getStation().equals(stationNumber)){
                for(Person person : persons){
                    if(person.getAddress().equals(fireStation.getAddress())){
                        personsCoveredByFireStationAddress.add(person);
                    }
                }
            }
        }
        return personsCoveredByFireStationAddress;
    }

    /**
     * This method helps getting a list of person covered by a station number
     * @param persons which is a list of persons 
     * @param address which a string representing an address
     * @return a list of persons covered by an address
     **/

    public static List<Person> getListOfPersonFromAddress(List<Person> persons, String address){
        List<Person> personsLivingAtAddress = new ArrayList<>();
        for(Person person : persons){
            if(person.getAddress().equalsIgnoreCase(address)){
                personsLivingAtAddress.add(person);
            }
        }
        return personsLivingAtAddress;
    }

    /**
     * This method is getting {@link MedicalRecord#birthDate} from a person and compute his age through {@link AlertsUtility#calculateAgeFromDate(LocalDate)}
     * @param medicalRecords which is a list retrieved from {@link MedicalRecordsDAO#getMedicalRecords()}
     * @param person which is an Object {@link Person} from whom we will retrieve birthdate field which is a LocalDate
     * @return an int which is the conversion of LocalDate brithDate into a computed age
     */

    public static int getPersonAgeFromMedicalRecords(List<MedicalRecord>medicalRecords, Person person){
        for(MedicalRecord medicalRecord: medicalRecords){
            if(medicalRecord.getFirstName().equals(person.getFirstName()) && medicalRecord.getLastName().equals(person.getLastName())){
                return calculateAgeFromDate(medicalRecord.getBirthDate());
            }
        }
        throw new PersonNotFoundException(String.format("No person was found in medical records with firstname %s and lastname %s", person.getFirstName(), person.getLastName()));
    }

    /**
     * This method retrieves medical data from a Person object ans stores it into a map with medications and allergies
     * @param medicalRecords which is a list retrieved from {@link MedicalRecordsDAO#getMedicalRecords()}
     * @param person which is an Object {@link Person} from whom we will retrieve medical data
     * @return
     */
    public static Map<String, List<String>> getPersonMedicalData(List<MedicalRecord>medicalRecords, Person person){
        Map<String, List<String>> medicalData = new HashMap<>();
        for(MedicalRecord medicalRecord: medicalRecords){
            if(medicalRecord.getFirstName().equals(person.getFirstName())&&medicalRecord.getLastName().equals(person.getLastName())){
                medicalData.put("medications",medicalRecord.getMedications());
                medicalData.put("allergies", medicalRecord.getAllergies());
                return medicalData;
            }
        }
        return medicalData;
    }

    /**
     * This method will be used to convert Object to json string to log payload
     * @param object
     * @return a string with jackson mapper to convert an object to a json string
     * @throws JsonProcessingException
     */
    public static String convertObjectToString(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
}
