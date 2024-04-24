package fi.tuni.prog3.weatherapp;

import java.io.IOException;

public class ReadWriteToFileTest {
    public static void main(String[] args) {
        
        // Test reading from files
        ReadWriteToFile readWriteToFile = new ReadWriteToFile();
        String folderName = "WeatherData";
        try {
            String[][] WeatherData = readWriteToFile.readFromFile(folderName);
            
            // Print the read weather data
            for (String[] data : WeatherData) {
                for (String line : data) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from files: " + e.getMessage());
        }

        // Test writing to a file
        String DataToWrite = "Test data to be written to a file.";
        readWriteToFile.setDataToWrite(DataToWrite);
        String fileName = "testFile.txt";
        try {
            if (readWriteToFile.writeToFile(fileName)) {
                System.out.println("Data written to file successfully.");
            } else {
                System.out.println("Failed to write data to file.");
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
