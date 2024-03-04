package net.safety.alerts.exceptions;

public class PersonNotFoundException extends RuntimeException {
    public PersonNotFoundException(String message) {

        super(message);
    }
}
