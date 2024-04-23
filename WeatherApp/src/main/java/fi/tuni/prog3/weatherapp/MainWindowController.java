
package fi.tuni.prog3.weatherapp;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class MainWindowController {
    @FXML
    private Button quitButton;
    @FXML
    private Pane UpperPane;
    @FXML
    private Button searchButton;
    @FXML
    private TextFlow CurrentLocation;
    @FXML
    private TextFlow CurrentLocation1;
    @FXML
    private ScrollPane hourlyForecast;
    @FXML
    private ScrollPane weeklyForecast;
    
    @FXML
    void Quit(ActionEvent event) {
        Stage stage = (Stage) quitButton.getScene().getWindow();
        stage.close();
    }
}
