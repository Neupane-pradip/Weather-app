
package fi.tuni.prog3.weatherapp;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import com.google.gson.JsonParser;

public class APIAccess implements iAPI {
    
    private boolean has_data = false;
    private String current_weather = "";
    private String[] dailyForecast = new String[96];
    private String[] hourlyForecast = new String[16];
    
    private static final String API_key = "f52059b774ff5f7508c3b449c6357b9c";
    private static final String test_coord_lon = "23.7609";
    private static final String test_coord_lat = "61.4981";
    
    public APIAccess(){
        callAPI();
    }
    
    private void callAPI(){
        String URL_string = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s"
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
            System.out.print(result);
            //JsonParser parse = new JsonParser();
            //JsonObject data = (JsonObject) parse.parse(result);
        }
        catch(IOException err){

        }
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
