package com.neo.manager.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.neo.manager.NEODataObjectsManager;
import com.neo.to.DataNEOObjectsTO;

/**
 * @author dev
 */
@RunWith(MockitoJUnitRunner.class)
public class NEODataObjectsManagerTest {

    // Class Under Test
    private NEODataObjectsManager cut;

    @Mock
    DataNEOObjectsTO to;
    private static final String APIKEY_PARAM = "&api_key=B30cILrIL32Px5Thm38q3JXxecfQBXmkBq1F7NU4";
    private static final String DETAIL_PARAM = "detailed=true";

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        cut = mock(NEODataObjectsManager.class, CALLS_REAL_METHODS);
    }

    @Test
    public void getTotalNEOObjectsCount_should_return_zeroNEOcount_when_stats_response_is_empty() throws Exception {

        final URL url = new URL("https://api.nasa.gov/neo/rest/v1/stats?" + APIKEY_PARAM);
        final String response = "";
        // WHEN
        doReturn(response).when(cut).getResponseString(url);
        final String count = cut.getTotalNEOObjectsCount();
        assertEquals(count, "0");

    }

    @Test
    public void getTotalNEOObjectsCount_should_return_totalNEOcount_when_stats_response_is_notEmpty() throws Exception {

        final URL url = new URL("https://api.nasa.gov/neo/rest/v1/stats?" + APIKEY_PARAM);
        final String response = "{\"near_earth_object_count\":20355}";
        // WHEN
        doReturn(response).when(cut).getResponseString(url);
        final String count = cut.getTotalNEOObjectsCount();
        assertEquals(count, "20355");
    }

    @Test
    public void getLargestNEOObject_should_return_largest_NEO_data_as_string() throws Exception {

        final URL url = new URL("https://api.nasa.gov/neo/rest/v1/neo/2001036?" + DETAIL_PARAM + APIKEY_PARAM);
        final String response =
                "{\"id\":\"2001036\",\"neo_reference_id\":\"2001036\",\"name\":\"1036 Ganymed (A924 UB)\",\"name_limited\":\"Ganymed\",......}";
        // WHEN
        doReturn(response).when(cut).getResponseString(url);
        final String neoString = cut.getLargestNEOObject();
        assertEquals(response, neoString);
    }

    @Test
    public void getClosestNEOObject_should_return_closest_NEO_data_as_string() throws Exception {

        final URL url = new URL("https://api.nasa.gov/neo/rest/v1/neo/2001036?" + DETAIL_PARAM + APIKEY_PARAM);
        final String response =
                "{\"id\":\"2001036\",\"neo_reference_id\":\"2001036\",\"name\":\"1036 Ganymed (A924 UB)\",\"name_limited\":\"Ganymed\",......}";
        // WHEN
        doReturn(response).when(cut).getResponseString(url);
        doReturn(to).when(cut).retrieveNeoObjectsForToday();
        doReturn(to).when(cut).retrieveNeoObjectsForNextDate();
        final String neoString = cut.getClosestNeoObject();
        assertEquals(to.toString(), neoString);
    }
}
