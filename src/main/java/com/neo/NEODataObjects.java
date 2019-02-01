/**
 * 
 */
package com.neo;

import com.neo.manager.NEODataObjectsManager;

/**
 * NeoWs (Near Earth Object Web Service) is a RESTful web service for near earth Asteroid information. With NeoWs a user can: search for
 * Asteroids based on their closest approach date to Earth, lookup a specific Asteroid with its NASA JPL small body id, as well as browse the
 * overall data-set
 * 
 * @author dev
 */
public class NEODataObjects {

    /**
     * @param args
     */
    public static void main(final String[] args) {

        final NEODataObjectsManager manager = new NEODataObjectsManager();
        final Long count = manager.getTotalNEOObjectsCount();
        System.out.println("Total number of NEO objects :" + count);

        if (count != 0) {
            // Get closest NEO object data
            final String closestNEOData = manager.getClosestNeoObject();
            System.out.println("Closest NEO object data :" + closestNEOData);

            // Get largest NEO Object data
            final String largestNEOData = manager.getLargestNEOObject(args);
            System.out.println("Largest NEO object data :" + largestNEOData);

        } else {
            System.out.println("NEO WS is unavailable.");
        }

    }
}
