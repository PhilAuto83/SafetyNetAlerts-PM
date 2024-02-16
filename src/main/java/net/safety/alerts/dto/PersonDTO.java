package net.safety.alerts.dto;

import jakarta.validation.constraints.Pattern;

public class PersonDTO {

    private String firstName;
    private String lastName;
    private String address;
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$")
    private String phone;

    public PersonDTO(String firstName, String lastName, String address, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String toString(){
        return "['firstName' : "+firstName
                +" ,'lastName' : "+lastName
                +" ,'address' :"+address
                +" ,'phone' : "+phone;
    }
}
