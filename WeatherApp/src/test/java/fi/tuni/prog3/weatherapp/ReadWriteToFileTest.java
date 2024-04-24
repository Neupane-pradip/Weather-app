package fi.tuni.prog3.weatherapp;

import java.io.IOException;

public class ReadWriteToFileTest {
    public static void main(String[] args) {
        
        // Test reading from files
        ReadWriteToFile readWriteToFile = new ReadWriteToFile();
        String folderName = "weatherData";
        try {
            String[][] weatherData = readWriteToFile.readFromFile(folderName);
            
            // Print the read weather data
            for (String[] data : weatherData) {
                for (String line : data) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from files: " + e.getMessage());
        }

        // Test writing to a file
        String dataToWrite = "Test data to be written to a file.";
        readWriteToFile.setDataToWrite(dataToWrite);
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
