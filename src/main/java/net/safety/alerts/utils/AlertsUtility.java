package net.safety.alerts.utils;


import net.safety.alerts.model.FireStation;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.model.Person;

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
            if(person.getAddress().equals(address)){
                personsLivingAtAddress.add(person);
            }
        }
        return personsLivingAtAddress;
    }

}
