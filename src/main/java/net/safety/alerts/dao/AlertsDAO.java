package net.safety.alerts.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.safety.alerts.model.AlertsData;
import net.safety.alerts.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class AlertsDAO {

    ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(AlertsDAO.class);

    public static AlertsData getData()  {
        AlertsData alertsData = null;
        try {
            logger.info("Trying to access data.json file in src/main/resources folder.");
            alertsData = mapper.readValue(new File("src/main/resources/data.json"), AlertsData.class);
            logger.debug("List of persons retrieved from folder AlertsData Object :"+alertsData.getPersons());
            logger.debug("List of firestations retrieved from folder AlertsData Object :"+alertsData.getFireStations());
            logger.debug("List of medicalrecords retrieved from folder AlertsData Object :"+alertsData.getMedicalRecords());
            return alertsData;
        } catch (IOException e) {
           logger.error(e.toString());
        }
        return alertsData;
    }

    public static void save(AlertsData alertsData){
        logger.info("Write AlertsData Object to file");
        try {
            logger.debug("AlertsData object contains a list of persons :"+alertsData.getPersons());
            logger.debug("AlertsData object contains a list of firestations :"+alertsData.getFireStations());
            logger.debug("AlertsData object contains a list of medicalrecords :"+alertsData.getMedicalRecords());
            mapper.writeValue(new File("src/main/resouces/data.json"),alertsData);
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

}
