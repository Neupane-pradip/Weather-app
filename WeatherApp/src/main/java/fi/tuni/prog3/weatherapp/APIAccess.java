
package fi.tuni.prog3.weatherapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import com.google.gson.JsonParser;
import java.time.Instant;

public class APIAccess implements iAPI {
    
    private boolean has_current_info = false;
    private String current_weather = "";
    
    private static final String API_key = "f52059b774ff5f7508c3b449c6357b9c";
    private static final String FORECAST_DAYS = "7";
    private static final String test_coord_lon = "23.7609";
    private static final String test_coord_lat = "61.4981";
    
    public APIAccess(){}
    
    private void callAPIForCurrent( double latitude, double longitude){
        String URL_string = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric"
                ,latitude, longitude, API_key);
        try{
            URL url = new URL(URL_string);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader (connection.getInputStream()));
            
            String result = "";
            String line = "";
            while ((line = reader.readLine()) != null){
                result += line;
            }
            
            JsonParser parse = new JsonParser();
            JsonObject data = (JsonObject) parse.parse(result);
            saveCurrentData(data);
        }
        catch(IOException err){

        }
    }
    
    private void saveCurrentData(JsonObject data){
        
        String result = "";
        
        JsonArray weather = data.getAsJsonArray("weather");
        result += weather.get(0).getAsJsonObject().get("main").getAsString() + ";";
        result += weather.get(0).getAsJsonObject().get("description").getAsString() + ";";
        
        JsonObject main = data.getAsJsonObject("main");
        result += main.get("temp").getAsString() + ";";
        result += main.get("feels_like").getAsString() + ";";
        
        JsonObject clouds = data.getAsJsonObject("clouds");
        result += clouds.get("all").getAsString() + ";";
        
        JsonObject wind = data.getAsJsonObject("wind");
        result += wind.get("speed").getAsString() + ";";
        
        if (data.has("rain")){
            JsonObject rain = data.getAsJsonObject("rain");
            result += rain.get("1h").getAsString();
        }
        result += ";";
        if (data.has("snow")){
            JsonObject snow = data.getAsJsonObject("snow");
            result += snow.get("1h").getAsString();
        }
        result += ";";
        
        JsonObject system = data.getAsJsonObject("sys");
        long timeZoneOffset = data.get("timezone").getAsLong();
        result += UTC_UNIXConverter(system.get("sunrise").getAsString(), timeZoneOffset) + ";";
        result += UTC_UNIXConverter(system.get("sunset").getAsString(), timeZoneOffset);
        
        current_weather = result;
        has_current_info = true;
    }
    
    private void callAPIForForecast(double latitude, double longitude){
        
        String URL_stringHour = String.format(
                "https://pro.openweathermap.org/data/2.5/forecast/hourly?lat=%f&lon=%f&appid=%s&units=metric"
                ,latitude, longitude, API_key);
        String URL_stringDay = String.format(
                "https://api.openweathermap.org/data/2.5/forecast/daily?lat=%f&lon=%f&cnt=%s&appid=%s&units=metric"
                ,latitude, longitude, FORECAST_DAYS, API_key);
        try{
            URL urlHour = new URL(URL_stringHour);
            URL urlDay = new URL(URL_stringDay);
            URLConnection connectionHour = urlHour.openConnection();
            URLConnection connectionDay = urlDay.openConnection();
            BufferedReader readerHour = new BufferedReader(new InputStreamReader (connectionHour.getInputStream()));
            BufferedReader readerDay = new BufferedReader(new InputStreamReader (connectionDay.getInputStream()));
            
            String[] result = new String[2];
            result[0] = "";
            result[1] = "";
            String line = "";
            
            while ((line = readerDay.readLine()) != null){
                result[0] += line;
            }
            while ((line = readerHour.readLine()) != null){
                result[1] += line;
            }
            
            JsonParser parse = new JsonParser();
            JsonObject dataDay = (JsonObject) parse.parse(result[0]);
            JsonObject dataHour = (JsonObject) parse.parse(result[1]);
            saveForecastData(dataDay, dataHour);
        }
        catch(IOException err){

        }
    }
    
    private void saveForecastData(JsonObject dataDay, JsonObject dataHour){
        
    }
    
    
    private String UTC_UNIXConverter(String input, long timeZone){
        Instant time = Instant.ofEpochSecond(Long.parseLong(input) + timeZone);
        return time.toString().substring(11,16);
    }

    @Override
    public String lookUpLocation(String loc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getCurrentWeather(double lat, double lon) {
        if (!has_current_info){
           callAPIForCurrent(lat, lon); 
        }
        return current_weather;
    }

    @Override
    public String[][] getForecast(double lat, double lon) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
