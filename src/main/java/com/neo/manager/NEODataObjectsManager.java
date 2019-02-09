package com.neo.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.neo.to.DataNEOObjectsTO;

import org.apache.commons.lang3.StringUtils;

/**
 * NeoWs (Near Earth Object Web Service) is a RESTful web service for near earth Asteroid information. With NeoWs a user can: search for
 * Asteroids based on their closest approach date to Earth, lookup a specific Asteroid with its NASA JPL small body id, as well as browse the
 * overall data-set. This manager makes the API calls to retrieve the NEO information.
 */
public class NEODataObjectsManager {

    // In astronomical terms,Minimum orbit intersection distance (MOID) is a measure to assess potential close approaches and collision risks
    // between astronomical objects.
    private static final String MIN_DIST = "minimum_orbit_intersection";

    // query params to use
    private static final String DETAIL_PARAM = "detailed=true";
    private static final String APIKEY_PARAM = "&api_key=B30cILrIL32Px5Thm38q3JXxecfQBXmkBq1F7NU4";

    String nextDateUrl = null;
    String defaultErrorMsg = "Date Format Exception - Expected format (yyyy-mm-dd) - The Feed date limit is only 7 Days";
    private static final Logger LOGGER = Logger.getLogger(NEODataObjectsManager.class.getName());

    /**
     * Retrieve the total count of NEO objects.
     */
    @SuppressWarnings("deprecation")
    public Long getTotalNEOObjectsCount() {
        try {
            final URL url = new URL("https://api.nasa.gov/neo/rest/v1/stats?" + APIKEY_PARAM);
            final String response = getResponseString(url);
            if (StringUtils.isNotEmpty(response)) {
                final JSONParser parser = new JSONParser();
                final JSONObject json = (JSONObject) parser.parse(response);
                for (final Map.Entry<String, Object> entry : json.entrySet()) {
                    if (entry.getKey().equals("near_earth_object_count")) {
                        return Long.parseLong(entry.getValue().toString());
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
        }
        return 0l;
    }

    /**
     * Returns the details of closest NEO.
     * 
     * @param args
     * @return
     * @throws IOException
     */
    public String getClosestNeoObject() {
        // First look for the NEO objects feed for today,if not found then check for the next day and once found loop through them to find the
        // NEO with smallest "minimum_orbit_intersection" in AU.
        DataNEOObjectsTO to = retrieveNeoObjectsForToday();
        while (null == to) {
            to = retrieveNeoObjectsForNextDate();
        }
        // Return the details of the closest NEO object as a string.
        return to.toString();
    }

    /**
     * Retrieves a list of Asteroids based on their closest approach date to Earth for today and returns the details of the closest NEO.
     * 
     * @return DataNEOObjectsTO
     */
    public DataNEOObjectsTO retrieveNeoObjectsForToday() {
        try {
            final URL url = new URL("https://api.nasa.gov/neo/rest/v1/feed/today?" + DETAIL_PARAM + APIKEY_PARAM);
            final String response = getResponseString(url);
            return processClosestNEOObjectsData(response);
        } catch (final MalformedURLException e) {
            LOGGER.severe(e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a list of Asteroids based on their closest approach date to Earth for selected period and returns the details of the closest
     * NEO.
     * 
     * @param startDate
     * @param endDate
     * @return DataNEOObjectsTO
     */
    public DataNEOObjectsTO retrieveNeoObjectsForNextDate() {
        try {
            final URL url = new URL(nextDateUrl);
            final String response = getResponseString(url);
            return processClosestNEOObjectsData(response);
        } catch (final MalformedURLException e) {
            LOGGER.severe(e.getMessage());
        }
        return null;
    }

    /**
     * Process the response and return the details of the closest NEO.
     * 
     * @param response
     */
    @SuppressWarnings("deprecation")
    private DataNEOObjectsTO processClosestNEOObjectsData(final String response) {
        try {
            if (StringUtils.isNotEmpty(response)) {
                final JSONParser parser = new JSONParser();
                final JSONObject json = (JSONObject) parser.parse(response);

                for (final Map.Entry<String, Object> entry : json.entrySet()) {

                    if (entry.getKey().equals("near_earth_objects")) {
                        return extractNEOData(entry);
                    } else if (entry.getKey().equals("links")) {
                        extractNextUrl(entry);
                    }
                }
            }
        } catch (final ParseException e) {
            LOGGER.severe(e.getMessage());
        }
        return null;
    }

    /**
     * @param parser
     * @param entry
     * @throws ParseException
     */
    @SuppressWarnings("deprecation")
    private void extractNextUrl(final Map.Entry<String, Object> links) throws ParseException {
        final JSONParser parser = new JSONParser();
        nextDateUrl = null;
        final JSONObject linksJson = (JSONObject) parser.parse(links.getValue().toString());
        for (final Map.Entry<String, Object> urls : linksJson.entrySet()) {
            if (urls.getKey().equals("next")) {
                nextDateUrl = urls.getValue().toString();
                break;
            }
        }
    }

    /**
     * @param parser
     * @param neos
     * @throws ParseException
     * @throws IOException
     * @throws JsonParseException
     * @throws JsonMappingException
     */
    @SuppressWarnings("deprecation")
    private DataNEOObjectsTO extractNEOData(final Map.Entry<String, Object> neos) {
        try {
            final JSONParser parser = new JSONParser();
            final JSONObject neoJson = (JSONObject) parser.parse(neos.getValue().toString());

            if (!neoJson.isEmpty()) {
                for (final Map.Entry<String, Object> dateEntry : neoJson.entrySet()) {

                    final JSONArray array = (JSONArray) parser.parse(dateEntry.getValue().toString());
                    final ObjectMapper mapper = new ObjectMapper();

                    final List<DataNEOObjectsTO> responseTO = mapper.readValue(array.toString(), new TypeReference<List<DataNEOObjectsTO>>() {
                    });
                    return getClosestNEOObjectData(responseTO);
                }
            }
        } catch (final ParseException | IOException e) {
            LOGGER.severe(e.getMessage());
        }
        return null;
    }

    /**
     * Returns the details of the closest NEO.
     * 
     * @param responseTO
     * @return DataNEOObjectsTO
     */
    private DataNEOObjectsTO getClosestNEOObjectData(final List<DataNEOObjectsTO> responseTO) {
        Double minDistance = 0.0;
        for (final DataNEOObjectsTO to : responseTO) {
            if (!to.getOrbitalData().isEmpty()) {
                final String minOrbitIntersection = (String) to.getOrbitalData().get(MIN_DIST);
                // for the first record,minDistance variable at this point is 0.0 and should be assigned the value from the first record
                if (minDistance == 0.0) {
                    minDistance = Double.parseDouble(minOrbitIntersection);
                } else if (Double.parseDouble(minOrbitIntersection) < minDistance) {
                    minDistance = Double.parseDouble(minOrbitIntersection);
                }
            }
        }
        for (final DataNEOObjectsTO to : responseTO) {
            if (!to.getOrbitalData().isEmpty()) {
                final String minOrbitIntersection = (String) to.getOrbitalData().get(MIN_DIST);
                if (Double.parseDouble(minOrbitIntersection) == minDistance) {
                    return to;
                }
            }
        }
        return null;
    }

    /**
     * Method to look up the largest NEO object data
     */
    public String getLargestNEOObject() {
        // Lookup a specific Asteroid based on its NASA JPL small body (SPK-ID) ID to determine the largest NEO
        // SPK-ID of the largest NEO is determined using the JPL Small-Body Database Search Engine https://ssd.jpl.nasa.gov/sbdb_query.cgi
        try {
            final URL url = new URL("https://api.nasa.gov/neo/rest/v1/neo/2001036?" + DETAIL_PARAM + APIKEY_PARAM);

            final String response = getResponseString(url);

            if (StringUtils.isNotEmpty(response)) {
                return response;
            }
        } catch (final MalformedURLException e) {
            LOGGER.severe(e.getMessage());
        }
        return "Cannot find largest NEO Object";
    }

    /**
     * @param url
     */
    public String getResponseString(final URL url) {
        HttpURLConnection connection = null;
        final StringBuilder contents = new StringBuilder();

        try {
            // Configure a connection for a GET request
            connection = (HttpURLConnection) url.openConnection();
            // Check the result
            if (connection.getResponseCode() != 200) {
                if (null != connection.getResponseMessage()) {
                    throw new IOException(connection.getResponseMessage());
                } else {
                    throw new IOException(defaultErrorMsg);
                }
            }
            // Read the response body
            final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));
            String line;
            while ((line = in.readLine()) != null) {
                contents.append(line);
            }
            in.close();
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return contents.toString();
    }
}