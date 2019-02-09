/**
 * 
 */
package com.neo;

import java.util.logging.Logger;

import com.neo.manager.NEODataObjectsManager;

/**
 * NeoWs (Near Earth Object Web Service) is a RESTful web service for near earth Asteroid information. With NeoWs a user can: search for
 * Asteroids based on their closest approach date to Earth, lookup a specific Asteroid with its NASA JPL small body id, as well as browse the
 * overall data-set
 * 
 * @author dev
 */
public class NEODataObjects {
    private static final Logger LOGGER = Logger.getLogger(NEODataObjects.class.getName());

    /**
     * @param args
     */
    public static void main(final String[] args) {

        final NEODataObjectsManager manager = new NEODataObjectsManager();
        final String count = manager.getTotalNEOObjectsCount();
        LOGGER.info("Total number of NEO objects :" + count);

        if (!count.equalsIgnoreCase("0")) {
            // Get closest NEO object data
            final String closestNEOData = manager.getClosestNeoObject(args);
            LOGGER.info("Closest NEO object data :" + closestNEOData);

            // Get largest NEO Object data
            final String largestNEOData = manager.getLargestNEOObject();
            LOGGER.info("Largest NEO object data :" + largestNEOData);

        } else {
            LOGGER.info("NEO WS is unavailable.");
        }

    }
}
