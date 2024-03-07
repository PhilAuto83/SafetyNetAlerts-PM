package net.safety.alerts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import net.safety.alerts.exceptions.PersonNotFoundException;
import net.safety.alerts.model.Person;
import net.safety.alerts.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
public class PersonController {

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private PersonService personService;

    @PostMapping(value = "/person", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) throws JsonProcessingException {
        logger.info("Request launched to create a person the following person : {}", person.getFirstName()+" "+person.getLastName());
        if(personService.doesPersonAlreadyExist(person)){
            logger.debug("Person with firstname {} and lastname {} already exists in file", person.getFirstName(), person.getLastName());
            return ResponseEntity.noContent().build();
        }
        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();

        return ResponseEntity.created(currentUri).body(personService.save(person));
    }

    @PutMapping(value = "/person", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<Person> update(@Valid @RequestBody Person person) throws JsonProcessingException {
        logger.info("Request launched to update the following person : {}", person.getFirstName()+" "+person.getLastName());
        if(!personService.doesPersonAlreadyExist(person)){
            logger.error("No person found with firstname {} and lastname {}", person.getFirstName(), person.getLastName());
            throw new PersonNotFoundException(String.format("No person found with firstname %s and lastname %s", person.getFirstName(), person.getLastName()));
        }
        return ResponseEntity.ok(personService.update(person));
    }

    @DeleteMapping(value ="/person/{firstname}/{lastname}", produces = "application/json")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("firstname") String firstName, @PathVariable("lastname") String lastName) throws JsonProcessingException {

        logger.info("Request launched to delete the following person {}", firstName+" "+lastName);
        if(!personService.areFirstNameAndLastnamePresent(firstName, lastName)) {
            logger.error("No person found with firstname \"{}\" and lastname \"{}\"", firstName, lastName);
            throw new PersonNotFoundException(String.format("No person found with firstname %s and lastname %s", firstName, lastName));
        }
        personService.remove(firstName, lastName);
        Map<String, String> response = new HashMap<>();
        response.put("date", new Date().toString());
        response.put("message", String.format("Person with firstname %s and lastname %s has been deleted", firstName, lastName));
        return ResponseEntity.ok(response);
    }
}
