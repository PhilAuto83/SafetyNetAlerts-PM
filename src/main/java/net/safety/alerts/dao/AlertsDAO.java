package net.safety.alerts.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.safety.alerts.model.AlertsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


public class AlertsDAO {

    private final static ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(AlertsDAO.class);

    public static AlertsData getData()  {
        AlertsData alertsData = null;
        try {
            logger.info("Trying to access data.json file in src/main/resources folder.");
            alertsData = mapper.readValue(new File("src/main/resources/data.json"), AlertsData.class);
            logger.debug("List of persons retrieved from folder AlertsData Object :"+alertsData.getPersons());
            logger.debug("List of fire stations retrieved from folder AlertsData Object :"+alertsData.getFireStations());
            logger.debug("List of medical records retrieved from folder AlertsData Object :"+alertsData.getMedicalRecords());
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
            logger.debug("AlertsData object contains a list of fire stations :"+alertsData.getFireStations());
            logger.debug("AlertsData object contains a list of medical records :"+alertsData.getMedicalRecords());
            mapper.writeValue(new File("src/main/resources/data.json"),alertsData);
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

}
