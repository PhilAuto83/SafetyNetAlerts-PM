package net.safety.alerts.integration;

import net.safety.alerts.service.FireStationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class FireStationServiceWithDaoIT {


    @Autowired
    private FireStationService fireStationService;

    @ParameterizedTest(name = "Check method doesStationNumberExist() returns true when station number is {0}")
    @ValueSource(ints={1,2,3,4})
    @DisplayName("Check method doesStationNumberExist() return true if station number is in the list")
    public void givenStationNumberExists_whenCallingDoesStationNumberExistMethod_ThenReturnTrueTest(int stationNumber){
        assertTrue(fireStationService.doesStationNumberExist(stationNumber));
    }

    @Test
    @DisplayName("Check method doesStationNumberExist() return false if station number 7 is not in the list")
    public void givenStationNumberNotExists_whenCallingDoesStationNumberExistMethod_ThenReturnFalseTest(){
        assertFalse(fireStationService.doesStationNumberExist(7));
    }
}
