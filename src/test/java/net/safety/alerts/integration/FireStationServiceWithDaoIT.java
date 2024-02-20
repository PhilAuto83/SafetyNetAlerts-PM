package net.safety.alerts.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.safety.alerts.dao.AlertsDAO;
import net.safety.alerts.dto.PersonByFireStation;
import net.safety.alerts.dto.PersonDTO;
import net.safety.alerts.service.FireStationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FireStationServiceWithDaoIT {


    @Autowired
    private FireStationService fireStationService;

    @BeforeAll
    public static void setUpDataSource(){
        AlertsDAO.setFilePath("src/test/resources/data-test.json");
    }

    @AfterAll
    public static void RollbackDataSource(){
        AlertsDAO.setFilePath("src/main/resources/data.json");
    }


    @ParameterizedTest(name = "Check method doesStationNumberExist() returns true when station number is {0}")
    @ValueSource(strings={"1","2","3","4"})
    @DisplayName("Check method doesStationNumberExist() return true if station number is in the list")
    public void givenStationNumberExists_whenCallingDoesStationNumberExistMethod_ThenReturnTrueTest(String stationNumber){
        assertTrue(fireStationService.doesStationNumberExist(stationNumber));
    }

    @Test
    @DisplayName("Check method doesStationNumberExist() return false if station number 7 is not in the list")
    public void givenStationNumberNotExists_whenCallingDoesStationNumberExistMethod_ThenReturnFalseTest(){
        assertFalse(fireStationService.doesStationNumberExist("7"));
    }

    @Test
    @DisplayName("Checking station number returning empty list of PersonByFireStation")
    public void givenStationNumber_whenCallingGetPersonsInfoByStationNumber_thenReturnEmptyList() throws JsonProcessingException {
        PersonByFireStation personByFireStation = fireStationService.getPersonsInfoByStationNumber("6");
        assertEquals(Collections.emptyList(),personByFireStation.getPersons());
        assertEquals(0,personByFireStation.getNbChildren());
        assertEquals(0,personByFireStation.getNbAdults());
    }

    @Test
    @DisplayName("Check person list content and nb of adults and children for station 1")
    public void givenStationNumberOneIsRequested_whenCallingGetPersonsInfoByStationNumber_thenCheckResultIsNotNull() throws JsonProcessingException {

        PersonByFireStation personByFireStation = fireStationService.getPersonsInfoByStationNumber("1");
        assertNotNull(personByFireStation);
        assertEquals(5, personByFireStation.getNbAdults());
        assertEquals(1, personByFireStation.getNbChildren());
    }
}
