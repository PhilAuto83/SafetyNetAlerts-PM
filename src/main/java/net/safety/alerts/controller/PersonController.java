package net.safety.alerts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

        if(personService.doesPersonAlreadyExist(person)){
            logger.debug("Person with firstname {} and lastname {} already exists in file", person.getFirstName(), person.getLastName());
            return ResponseEntity.noContent().build();
        }
        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();
        logger.info("Request to create a person launched : {}", currentUri);
        logger.info("Request payload : {}", mapper.writeValueAsString(person));

        return ResponseEntity.created(currentUri).body(personService.save(person));
    }

    @PutMapping(value = "/person", produces = {"application/json"}, consumes = {"application/json"})
    public ResponseEntity<Person> update(@Valid @RequestBody Person person) throws JsonProcessingException {

        if(!personService.doesPersonAlreadyExist(person)){
            logger.error("No person found with firstname {} and lastname {}", person.getFirstName(), person.getLastName());
            throw new PersonNotFoundException(String.format("No person found with firstname %s and lastname %s", person.getFirstName(), person.getLastName()));
        }
        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand()
                .toUri();
        logger.info("Request to update a person launched : {}", currentUri);
        logger.info("Request payload : {}", mapper.writeValueAsString(person));
        return ResponseEntity.ok(personService.update(person));
    }

    @DeleteMapping(value ="/person/{firstname}/{lastname}", produces = "application/json")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("firstname") String firstName, @PathVariable("lastname") String lastName) throws JsonProcessingException {

        URI currentUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{firstname}/{lastname}")
                .buildAndExpand(firstName, lastName)
                .toUri();
        logger.info("Request to delete a person launched : {}", currentUri);
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
