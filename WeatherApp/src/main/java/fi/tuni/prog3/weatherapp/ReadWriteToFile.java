package fi.tuni.prog3.weatherapp;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementation of the iReadWriteToFile interface for reading and
 * writing weather data to files.
 */
public class ReadWriteToFile implements iReadAndWriteToFile {

    private String dataToWrite;
    private String currentJsonData;
    private String hourlyJsonData;
    private String dailyJsonData;

    /**
     * Sets the data to be written to a file.
     *
     * @param data The data to be written.
     */
  
    public void setDataToWrite(String data) {
        this.dataToWrite = data;
    }

    /**
     * Reads weather data from files in a specified folder.
     *
     * @param folderName The name of the folder containing weather data files.
     * @return True if reading is successful, false otherwise.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public String[][] readFromFile(String folderName) throws IOException {
        try {
            Path folderPath = Paths.get(folderName);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
                for (Path file : stream) {
                    String fileContent = Files.readString(file);
                    switch (file.getFileName().toString()) {
                        case "currentWeatherData":
                            currentJsonData = fileContent;
                            break;
                        case "hourlyWeatherData":
                            hourlyJsonData = fileContent;
                            break;
                        default:
                            dailyJsonData = fileContent;
                            break;
                    }
                }
                return new String[][]{currentJsonData.split("\n"), hourlyJsonData.split("\n"),
                dailyJsonData.split("\n")};
            }
        } catch (IOException e) {
            throw new IOException("Error reading from file: " + e.getMessage());
        }
    }

    /**
     * Writes the stored data to a file with the specified name.
     *
     * @param fileName The name of the file to which data will be written.
     * @return True if writing is successful, false otherwise.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public boolean writeToFile(String fileName) throws IOException {
        if (dataToWrite == null) {
            throw new IllegalStateException("No data set to write.");
        }

        String folderName = "weatherData";
        Path filePath = Paths.get(folderName, fileName);

        try {
            // Make sure the folder exists
            Files.createDirectories(filePath.getParent());  
            Files.writeString(filePath, dataToWrite);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            return false;
        }
    }
}
