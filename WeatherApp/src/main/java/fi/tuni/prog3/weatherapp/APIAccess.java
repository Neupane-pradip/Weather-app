
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

public class APIAccess implements iAPI {
    
    private boolean has_current_info = false;
    private String current_weather = "";
    
    private boolean has_forecast_info = false;
    private String[][] forecast;
    
    private static final String API_KEY = "f52059b774ff5f7508c3b449c6357b9c";
    private static final int FORECAST_DAYS = 7;
    private static final int FORECAST_HOURS = 96;
    private static final int DAILY_FORECAST_INDEX = 0;
    private static final int HOURLY_FORECAST_INDEX = 1;
    private static final int API_LOCATION_RESPONSE_LIMIT = 1;
    private static final int TIME_START_INDEX = 11;
    private static final int TIME_END_INDEX = 16;
    
    public APIAccess(){
        forecast = new String[2][];
        forecast[DAILY_FORECAST_INDEX ] = new String [FORECAST_DAYS];
        forecast[HOURLY_FORECAST_INDEX] = new String [FORECAST_HOURS];
    }
    
    private void callAPIForCurrent( double latitude, double longitude) throws Exception{
        String URL_string = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric"
                ,latitude, longitude, API_KEY);
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
            throw new Exception("API call was unsuccessful");
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
    
    private void callAPIForForecast(double latitude, double longitude) throws Exception{
        
        String URL_stringHour = String.format(
                "https://pro.openweathermap.org/data/2.5/forecast/hourly?lat=%f&lon=%f&appid=%s&units=metric"
                ,latitude, longitude, API_KEY);
        String URL_stringDay = String.format(
                "https://api.openweathermap.org/data/2.5/forecast/daily?lat=%f&lon=%f&cnt=%d&appid=%s&units=metric"
                ,latitude, longitude, FORECAST_DAYS, API_KEY);
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
            throw new Exception("API call was unsuccessful");
        }
    }
    
    private void saveForecastData(JsonObject dataDay, JsonObject dataHour){
        
        int index = 0;
        String result = "";
        
        for (JsonElement day : dataDay.get("list").getAsJsonArray()){
            
            JsonArray weather = day.getAsJsonObject().getAsJsonArray("weather");
            result += weather.get(0).getAsJsonObject().get("main").getAsString() + ";";
            
            JsonObject temperatures = day.getAsJsonObject().get("temp").getAsJsonObject();
            result += temperatures.get("min").getAsString() + ";";
            result += temperatures.get("max").getAsString() + ";";
            
            result += day.getAsJsonObject().get("clouds").getAsString() + ";";
            result += day.getAsJsonObject().get("speed").getAsString() + ";";
            
            if (day.getAsJsonObject().has("rain")){
                result += day.getAsJsonObject().get("rain").getAsString();
            }
            result += ";";
            if (day.getAsJsonObject().has("snow")){
                result += day.getAsJsonObject().get("snow").getAsString();
            }
            forecast[DAILY_FORECAST_INDEX][index] = result;
            result = "";
            index++;
        }
        index = 0;
        
        for (JsonElement hour : dataHour.get("list").getAsJsonArray()){
            
            JsonArray weather = hour.getAsJsonObject().getAsJsonArray("weather");
            result += weather.get(0).getAsJsonObject().get("main").getAsString() + ";";
            
            JsonObject main = hour.getAsJsonObject().get("main").getAsJsonObject();
            result += main.get("temp").getAsString() + ";";
            
            result += hour.getAsJsonObject().get("clouds").getAsJsonObject().get("all").getAsString() + ";";
            result += hour.getAsJsonObject().get("wind").getAsJsonObject().get("speed").getAsString() + ";";
            
            if (hour.getAsJsonObject().has("rain")){
                result += hour.getAsJsonObject().get("rain").getAsJsonObject().get("1h").getAsString();
            }
            result += ";";
            if (hour.getAsJsonObject().has("snow")){
                result += hour.getAsJsonObject().get("snow").getAsJsonObject().get("1h").getAsString();
            }
            forecast[HOURLY_FORECAST_INDEX][index] = result;
            result = "";
            index++;
        }
        has_forecast_info = true;
    }
    
    
    private String UTC_UNIXConverter(String input, long timeZone){
        Instant time = Instant.ofEpochSecond(Long.parseLong(input) + timeZone);
        return time.toString().substring(TIME_START_INDEX, TIME_END_INDEX);
    }

    @Override
    public String lookUpLocation(String location) throws Exception{

        String URL_string = String.format(
                "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=%d&appid=%s"
                ,location, API_LOCATION_RESPONSE_LIMIT, API_KEY);
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
            JsonArray cities = (JsonArray) parse.parse(result);
            if (cities.isEmpty()){
                return "";
            }
            JsonObject cityInfo = cities.get(0).getAsJsonObject();
            
            
            return String.format("%s;%s",
                    cityInfo.get("lat").getAsString(),
                    cityInfo.get("lon").getAsString());
        }
        catch(IOException err){
            throw new Exception("API call was unsuccessful");
        }
    }

    @Override
    public String getCurrentWeather(double lat, double lon) throws Exception {
        
        if (abs(lat) > 90 || abs(lon) > 180){
            throw new IllegalArgumentException();
        }  
        if (!has_current_info){
           callAPIForCurrent(lat, lon); 
        }
        return current_weather;
    }

    @Override
    public String[][] getForecast(double lat, double lon) throws Exception {
        
        if (abs(lat) > 90 || abs(lon) > 180){
            throw new IllegalArgumentException();
        }        
        if (!has_forecast_info){
            callAPIForForecast(lat, lon);
        }
        return forecast;
    }
    
}
