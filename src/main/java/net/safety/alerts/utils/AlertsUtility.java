package net.safety.alerts.utils;


import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import net.safety.alerts.exceptions.PersonNotFoundException;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.model.Person;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.YEARS;

public final class AlertsUtility {

    public static int calculateAgeFromDate(LocalDate birthDate){
        return (int)YEARS.between(birthDate, LocalDate.now());
    }

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

    public static List<Person> getListOfPersonFromAddress(List<Person> persons, String address){
        List<Person> personsLivingAtAddress = new ArrayList<>();
        for(Person person : persons){
            if(person.getAddress().equalsIgnoreCase(address)){
                personsLivingAtAddress.add(person);
            }
        }
        return personsLivingAtAddress;
    }

    public static int getPersonAgeFromMedicalRecords(List<MedicalRecord>medicalRecords, Person person){
        for(MedicalRecord medicalRecord: medicalRecords){
            if(medicalRecord.getFirstName().equals(person.getFirstName()) && medicalRecord.getLastName().equals(person.getLastName())){
                return calculateAgeFromDate(medicalRecord.getBirthDate());
            }
        }
        throw new PersonNotFoundException(String.format("No person was found in medical records with firstname %s and lastname %s", person.getFirstName(), person.getLastName()));
    }

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
}
