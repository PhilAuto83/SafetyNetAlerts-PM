package net.safety.alerts.dto;

import java.util.List;

public class PersonByFireStation {

    List<PersonDTO> persons;
    int nbAdults;
    int nbChildren;

    public PersonByFireStation(List<PersonDTO> persons, int nbAdults, int nbChildren) {
        this.persons = persons;
        this.nbAdults = nbAdults;
        this.nbChildren = nbChildren;
    }

    public List<PersonDTO> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonDTO> persons) {
        this.persons = persons;
    }

    public int getNbAdults() {
        return nbAdults;
    }

    public void setNbAdults(int nbAdults) {
        this.nbAdults = nbAdults;
    }

    public int getNbChildren() {
        return nbChildren;
    }

    public void setNbChildren(int nbChildren) {
        this.nbChildren = nbChildren;
    }
}
