
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
    
    private static final String API_key = "f52059b774ff5f7508c3b449c6357b9c";
    private static final String test_coord_lon = "23.7609";
    private static final String test_coord_lat = "61.4981";
    
    public APIAccess(){
        callAPIForCurrent();
    }
    
    private void callAPIForCurrent(){
        String URL_string = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric"
                ,test_coord_lat, test_coord_lon, API_key);
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
        
        System.out.println(result);
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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String[][] getForecast(double lat, double lon) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
