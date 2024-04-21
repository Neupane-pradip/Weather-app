
package fi.tuni.prog3.weatherapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import com.google.gson.JsonParser;
import static java.lang.Math.abs;
import java.time.Instant;

/**
 * @author Hirvijärvi
 * 
 * Makes api calls for current weather, weather forecast and goelocation,
 * and converts the responses from Json format to Strings or String arrays.
 * All public methods throw Exception if the api call fails
 */
public class API implements iAPI {
    
    private String[][] forecast;
    private String current_weather;
    
    private static final String API_KEY = "f52059b774ff5f7508c3b449c6357b9c";
    private static final int FORECAST_DAYS = 7;
    private static final int FORECAST_HOURS = 96;
    private static final int DAILY_FORECAST_INDEX = 0;
    private static final int HOURLY_FORECAST_INDEX = 1;
    private static final int API_LOCATION_RESPONSE_LIMIT = 1;
    private static final int TIME_START_INDEX = 11;
    private static final int TIME_END_INDEX = 16;
    
    public API(){
        forecast = new String[2][];
        forecast[DAILY_FORECAST_INDEX ] = new String [FORECAST_DAYS];
        forecast[HOURLY_FORECAST_INDEX] = new String [FORECAST_HOURS];
    }
    /**
     * Makes an api call for current weather
     * and puts the response in to a Json object
     * @param latitude
     * @param longitude
     * @throws Exception if API call fails
     */
    private void callAPIForCurrent( double latitude, double longitude) throws Exception{
        String URL_string = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric"
                ,latitude, longitude, API_KEY);     // <- API call paramters
        try{
            // makes API connection
            URL url = new URL(URL_string);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader (connection.getInputStream()));
            
            String result = "";
            String line = "";
            // API response is read to a string
            while ((line = reader.readLine()) != null){
                result += line;
            }
            reader.close();
            
            // string is converted to Json Object
            JsonParser parse = new JsonParser();
            JsonObject data = (JsonObject) parse.parse(result);
            saveCurrentData(data);
        }
        catch(IOException err){
            throw new Exception("API call was unsuccessful");
        }
    }
    /**
     * converts all necessary information the Json object to a string
     * @param data 
     */
    private void saveCurrentData(JsonObject data){
        
        String result = "";
        
        // weaathr type and weathr description
        JsonArray weather = data.getAsJsonArray("weather");
        result += weather.get(0).getAsJsonObject().get("main").getAsString() + ";";
        result += weather.get(0).getAsJsonObject().get("description").getAsString() + ";";
        
        // temperature and feels like temperature
        JsonObject main = data.getAsJsonObject("main");
        result += main.get("temp").getAsString() + ";";
        result += main.get("feels_like").getAsString() + ";";
        
        // cloud %
        JsonObject clouds = data.getAsJsonObject("clouds");
        result += clouds.get("all").getAsString() + ";";
        
        // wind speed
        JsonObject wind = data.getAsJsonObject("wind");
        result += wind.get("speed").getAsString() + ";";
        
        // rain volume from last hour
        if (data.has("rain")){
            JsonObject rain = data.getAsJsonObject("rain");
            result += rain.get("1h").getAsString();
        }
        result += ";";
        // snow volume from last hour
        if (data.has("snow")){
            JsonObject snow = data.getAsJsonObject("snow");
            result += snow.get("1h").getAsString();
        }
        result += ";";
        
        // sunrie and sunset times for today converted form
        // unix UTC to normal readable time
        JsonObject system = data.getAsJsonObject("sys");
        long timeZoneOffset = data.get("timezone").getAsLong();
        result += UTC_UNIXConverter(system.get("sunrise").getAsLong(), timeZoneOffset) + ";";
        result += UTC_UNIXConverter(system.get("sunset").getAsLong(), timeZoneOffset);
        
        current_weather = result;
    }
    /**
     * Makes two api calls for hourly forecast and daily forecast
     * and puts the responses in to seperate Json objects
     * @param latitude
     * @param longitude
     * @throws Exception if one of the API calls fail
     */
    private void callAPIForForecast(double latitude, double longitude) throws Exception{
        
        String URL_stringHour = String.format(
                "https://pro.openweathermap.org/data/2.5/forecast/hourly?lat=%f&lon=%f&appid=%s&units=metric"
                ,latitude, longitude, API_KEY);     // <- API call parameters

        String URL_stringDay = String.format(
                "https://api.openweathermap.org/data/2.5/forecast/daily?lat=%f&lon=%f&cnt=%d&appid=%s&units=metric"
                ,latitude, longitude, FORECAST_DAYS, API_KEY);      // <- API call parameters
        try{
            //makes API connections
            URL urlHour = new URL(URL_stringHour);
            URL urlDay = new URL(URL_stringDay);
            URLConnection connectionHour = urlHour.openConnection();
            URLConnection connectionDay = urlDay.openConnection();
            BufferedReader readerHour = new BufferedReader(new InputStreamReader (connectionHour.getInputStream()));
            BufferedReader readerDay = new BufferedReader(new InputStreamReader (connectionDay.getInputStream()));
            
            // API responses are read to strings
            String resultDay = "";
            String resultHour = "";
            String line = "";
            
            while ((line = readerDay.readLine()) != null){
                resultDay += line;
            }
            readerDay.close();
            while ((line = readerHour.readLine()) != null){
                resultHour += line;
            }
            readerHour.close();
            
            // Strings are converted to Json Objects
            JsonParser parse = new JsonParser();
            JsonObject dataDay = (JsonObject) parse.parse(resultDay);
            JsonObject dataHour = (JsonObject) parse.parse(resultHour);
            saveForecastData(dataDay, dataHour);
        }
        catch(IOException err){
            throw new Exception("API call was unsuccessful");
        }
    }
    /**
     * converts all necessary information to readable strings and saves
     * it to a two dimentional array
     * @param dataDay
     * @param dataHour 
     */
    private void saveForecastData(JsonObject dataDay, JsonObject dataHour){
            
        int index = 0;
        String result = "";
        
        // goes through every day for
        for (JsonElement day : dataDay.get("list").getAsJsonArray()){
            
            // weather type
            JsonArray weather = day.getAsJsonObject().getAsJsonArray("weather");
            result += weather.get(0).getAsJsonObject().get("main").getAsString() + ";";
            
            // minimum and maximum temperatures for the day
            JsonObject temperatures = day.getAsJsonObject().get("temp").getAsJsonObject();
            result += temperatures.get("min").getAsString() + ";";
            result += temperatures.get("max").getAsString() + ";";
            
            // cloud %
            result += day.getAsJsonObject().get("clouds").getAsString() + ";";
            // wind speed
            result += day.getAsJsonObject().get("speed").getAsString() + ";";
            // total rain volume for the day
            if (day.getAsJsonObject().has("rain")){
                result += day.getAsJsonObject().get("rain").getAsString();
            }
            // total snow volume for the day
            result += ";";
            if (day.getAsJsonObject().has("snow")){
                result += day.getAsJsonObject().get("snow").getAsString();
            }
            forecast[DAILY_FORECAST_INDEX][index] = result;
            result = "";
            index++;
        }
        index = 0;
        
        // goes through every hour
        for (JsonElement hour : dataHour.get("list").getAsJsonArray()){
            
            // weather type
            JsonArray weather = hour.getAsJsonObject().getAsJsonArray("weather");
            result += weather.get(0).getAsJsonObject().get("main").getAsString() + ";";
               
            // temperature
            JsonObject main = hour.getAsJsonObject().get("main").getAsJsonObject();
            result += main.get("temp").getAsString() + ";";
            
            // cloud%
            result += hour.getAsJsonObject().get("clouds").getAsJsonObject().get("all").getAsString() + ";";
            // wind speed
            result += hour.getAsJsonObject().get("wind").getAsJsonObject().get("speed").getAsString() + ";";
            
            // rain volume from last hour
            if (hour.getAsJsonObject().has("rain")){
                result += hour.getAsJsonObject().get("rain").getAsJsonObject().get("1h").getAsString();
            }
            // snow volume from last hour
            result += ";";
            if (hour.getAsJsonObject().has("snow")){
                result += hour.getAsJsonObject().get("snow").getAsJsonObject().get("1h").getAsString();
            }
            forecast[HOURLY_FORECAST_INDEX][index] = result;
            result = "";
            index++;
        }
    }
    
    /**
     * converts unix UTC to timezone adjusted readably time 
     * @param input 
     * @param timeZone
     * @return time as string (":" as delimiter)
     */
    private String UTC_UNIXConverter(long input, long timeZone){
        Instant time = Instant.ofEpochSecond(input + timeZone);
        return time.toString().substring(TIME_START_INDEX, TIME_END_INDEX);
    }
    /**
     * Returns coordinates for a location.
     * @param loc Name of the location for which coordinates should be fetched.
     * @return String in format:
     * "latitude;longtitude"
     * @throws java.lang.Exception
     */
    @Override
    public String lookUpLocation(String loc) throws Exception{

        String URL_string = String.format(
                "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=%d&appid=%s"
                ,loc, API_LOCATION_RESPONSE_LIMIT, API_KEY);   // <- API parameters
        try{
            // makes API connection
            URL url = new URL(URL_string);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader (connection.getInputStream()));
            
            // reads response to string
            String result = "";
            String line = "";
            while ((line = reader.readLine()) != null){
                result += line;
            }
            reader.close();
            
            // converts string to JsonArray
            JsonParser parse = new JsonParser();
            JsonArray cities = (JsonArray) parse.parse(result);
            
            // if no locations are found, function return empty string
            if (cities.isEmpty()){
                return "";
            }
            JsonObject cityInfo = cities.get(0).getAsJsonObject();
            
            // coordinates are returned in format "latitude;longitude"
            return String.format("%s;%s",
                    cityInfo.get("lat").getAsString(),
                    cityInfo.get("lon").getAsString());
        }
        catch(IOException err){
            throw new Exception("API call was unsuccessful");
        }
    }
    /**
     * Returns the current weather for the given coordinates.
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String in format:
     * "weather;weather_description;temperature;feels_like_temp;cloud%;wind_speed;rain(1h);snow(1h);sunrise;sunset"
     * @throws java.lang.Exception
     */
    @Override
    public String getCurrentWeather(double lat, double lon) throws Exception {
        
        // if coordinates are out of range
        if (abs(lat) > 90 || abs(lon) > 180){
            throw new IllegalArgumentException();
        }  
        callAPIForCurrent(lat, lon); 
        return current_weather;
    }
    /**
     * Returns a forecast for the given coordinates as two string arrays
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return String[][] in format:
     * String[0][]: "weather;temp_min;temp_max;cloud%;wind_speed;rain_total;snow_total"
     * String[1][]: "weather;temperature;cloud%;wind_speed;rain(1h);snow(1h)"
     * @throws java.lang.Exception
     */
    @Override
    public String[][] getForecast(double lat, double lon) throws Exception {
        
        // if coordinates are out of range
        if (abs(lat) > 90 || abs(lon) > 180){
            throw new IllegalArgumentException();
        }        
        callAPIForForecast(lat, lon);
        return forecast;
    }
}
