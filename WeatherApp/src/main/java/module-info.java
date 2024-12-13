module fi.tuni.progthree.weatherapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    
    opens fi.tuni.prog3.weatherapp to javafx.fxml;
    exports fi.tuni.prog3.weatherapp;    
}
