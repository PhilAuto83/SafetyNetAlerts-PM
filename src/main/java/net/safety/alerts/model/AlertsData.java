package net.safety.alerts.model;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public class AlertsData {

    private List<Person>persons;
    private List<FireStation> fireStations;
    private List<MedicalRecord>medicalRecords;

    public AlertsData(List<Person> persons, List<FireStation> fireStations, List<MedicalRecord> medicalRecords) {
        this.persons = persons;
        this.fireStations = fireStations;
        this.medicalRecords = medicalRecords;
    }

    public List<Person> getPersons() throws JsonProcessingException {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public List<FireStation> getFireStations() {
        return fireStations;
    }

    public void setFireStations(List<FireStation> fireStations) {
        this.fireStations = fireStations;
    }

    public List<MedicalRecord> getMedicalRecords()  {
        return medicalRecords;
    }

    public void setMedicalRecords(List<MedicalRecord> medicalRecords) {
        this.medicalRecords = medicalRecords;
    }


}
