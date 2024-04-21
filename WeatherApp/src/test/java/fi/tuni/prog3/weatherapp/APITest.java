
package fi.tuni.prog3.weatherapp;

import java.lang.IllegalArgumentException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * @author Kalle Hirvijärvi
 * Unit tests for API class
 */
public class APITest {
    
    private API api = new API();
    private double lon;
    private double lat;
    
    public APITest() throws Exception {
        String long_result = api.lookUpLocation("Tampere");
        String[] coords = long_result.split(";");  
        lat = Double.parseDouble(coords[0]);
        lon = Double.parseDouble(coords[1]);
    }
    /**
     * Tetst that API returns roughly correct coordinates 
     */
    @Test
    public void testLookUpLocation() {
        String short_result = String.format("%.2f;%.2f",lat, lon);  
        assertEquals("61,50;23,76", short_result);
    }
    /**
     * Tests that the string that includes the information
     * for current weather is in the right format
     * @throws Exception 
     */
    @Test
    public void testGetCurrentWeather() throws Exception {
        
        String result = api.getCurrentWeather(lat, lon);
        String[] data = result.split(";");
        assertTrue(data.length <= 10 || data.length >= 8);
        assertTrue(!data[0].contains("."));
        assertTrue(!data[1].contains("."));
        assertTrue(!data[4].contains("."));
        assertTrue(data[6].contains(".") || data[6].isBlank());
        assertTrue(data[7].contains(".") || data[7].isBlank());
        assertTrue(data[data.length - 2].contains(":"));
        assertTrue(data[data.length - 1].contains(":"));
    /**
     * Tests that the string arrays that include the information
     * for the forecats are in the right format
     * @throws Exception 
     */
    }
    @Test
    public void testForecast() throws Exception {
        String[][] result = api.getForecast(lat, lon);
        
        // tests format for first, last and fifth element
        dailyForecastDataFrameTest(result[0][0].split(";"));
        dailyForecastDataFrameTest(result[0][4].split(";"));
        dailyForecastDataFrameTest(result[0][result[0].length - 1].split(";"));
        
        // test format for firt, last, 13th and 56th element
        hourlyForecastDataFrameTest(result[1][0].split(";"));
        hourlyForecastDataFrameTest(result[1][12].split(";"));
        hourlyForecastDataFrameTest(result[1][55].split(";"));
        hourlyForecastDataFrameTest(result[1][result[1].length - 1].split(";"));
    }
    
    /**
     * Test for invalid coordinates
     */
    @Test
    public void testInvalidCoordinates(){
        assertThrows(IllegalArgumentException.class, () -> api.getCurrentWeather(-100, -100));
        assertThrows(IllegalArgumentException.class, () -> api.getCurrentWeather(60, 200));
    }
    /**
     * Test for non existant location
     * @throws Exception 
     */
    @Test
    public void testNonLocation() throws Exception {
        String result = api.lookUpLocation("-");
        assertEquals("", result);
    }
    /**
     * format test for one daily forecast
     * @param data 
     */
    private void dailyForecastDataFrameTest(String[] data){
        
        assertTrue(data.length <= 7 || data.length >= 5);
        assertTrue(!data[0].contains("."));
        assertTrue(!data[3].contains("."));
        if (data.length > 5){
            assertTrue(data[5].contains(".") || data[5].isBlank());
        }
        if (data.length > 6){
            assertTrue(data[6].contains(".") || data[6].isBlank());
        }
    }
    /**
     * format test for one hourly forecast
     * @param data 
     */
    private void hourlyForecastDataFrameTest(String[] data){
        
        assertTrue(data.length <= 6 || data.length >= 4);
        assertTrue(!data[0].contains("."));
        assertTrue(!data[2].contains("."));
        if (data.length > 4){
            assertTrue(data[4].contains(".") || data[4].isBlank());
        }
        if (data.length > 5){
            assertTrue(data[5].contains(".") || data[5].isBlank());
        }
    }
}
