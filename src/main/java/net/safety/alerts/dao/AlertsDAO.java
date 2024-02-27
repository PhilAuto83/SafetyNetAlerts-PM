package net.safety.alerts.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.safety.alerts.model.AlertsData;
import net.safety.alerts.model.FireStation;
import net.safety.alerts.model.MedicalRecord;
import net.safety.alerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


@Repository
public class AlertsDAO {

    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final Logger logger = LoggerFactory.getLogger(AlertsDAO.class);

    private static String filePath = "src/main/resources/data.json";


    protected static AlertsData getData()  {
        AlertsData alertsData = new AlertsData(new ArrayList<Person>(),new ArrayList<FireStation>(), new ArrayList<MedicalRecord>());
        try {
            alertsData = mapper.readValue(new File(AlertsDAO.filePath), AlertsData.class);
            return alertsData;
        } catch (IOException e) {
           logger.error(e.toString());
        }
        return alertsData;
    }

    protected static void save(AlertsData alertsData){
        logger.info("Write AlertsData Object to file");
        try {
            logger.debug("AlertsData object contains a list of persons : {}", alertsData.getPersons());
            logger.debug("AlertsData object contains a list of fire stations {}", alertsData.getFireStations());
            logger.debug("AlertsData object contains a list of medical records {}", alertsData.getMedicalRecords());
            mapper.writeValue(new File(AlertsDAO.filePath), alertsData);
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public static void setFilePath(String newFilePath){
        AlertsDAO.filePath = newFilePath;
    }

    public static String getFilePath(){
        return AlertsDAO.filePath;
    }

}
