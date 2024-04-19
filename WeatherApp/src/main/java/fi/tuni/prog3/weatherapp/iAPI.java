package fi.tuni.prog3.weatherapp;

/**
 * Interface for extracting data from the OpenWeatherMap API.
 */
public interface iAPI {

    /**
     * Returns coordinates for a location.
     * @param loc Name of the location for which coordinates should be fetched.
     * @return String.
     * @throws java.lang.Exception
     */
    public String lookUpLocation(String loc) throws Exception;

    /**
     * Returns the current weather for the given coordinates.
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String.
     * @throws java.lang.Exception
     */
    public String getCurrentWeather(double lat, double lon) throws Exception;

    /**
     * Returns a forecast for the given coordinates.
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String.
     * @throws java.lang.Exception
     */
    public String[][] getForecast(double lat, double lon) throws Exception;
}
