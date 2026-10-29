# JavaFX Weather App

A Tampere University Programming 3 group course project with a JavaFX interface, OpenWeather API integration, and local weather-data file handling.

## Current status

This repository contains a **partial desktop application**. The JavaFX launcher loads the FXML window, while separate classes implement API access and file storage.

The committed window controller currently handles quitting the application. Its search and forecast controls are not connected to the API implementation, so launching the UI does not provide a complete interactive weather experience.

## Components

| Location under `WeatherApp/` | Purpose |
|---|---|
| `src/main/java/fi/tuni/prog3/weatherapp/WeatherApp.java` | JavaFX application entry point |
| `src/main/resources/fi/tuni/prog3/weatherapp/MainWindow.fxml` | Window layout |
| `src/main/java/fi/tuni/prog3/weatherapp/MainWindowController.java` | UI controller |
| `src/main/java/fi/tuni/prog3/weatherapp/API.java` | Location lookup, current weather, and forecasts |
| `src/main/java/fi/tuni/prog3/weatherapp/ReadWriteToFile.java` | Local data read/write implementation |
| `src/test/java/fi/tuni/prog3/weatherapp/` | API and file-handling tests |
| `pom.xml` | Maven dependencies and JavaFX launcher configuration |

The API class requests metric weather values, up to 24 hourly forecast entries, and seven daily forecast entries. Availability depends on the API account and endpoint access.

## Build and launch

Requirements: JDK 17, Apache Maven, a graphical desktop, and internet access for Maven dependency downloads.

```sh
git clone https://github.com/Neupane-pradip/Weather-app.git
cd Weather-app/WeatherApp
mvn compile
mvn javafx:run
```

The POM declares JavaFX 19 and Gson. Its Java source/target properties are 17, while the compiler plugin also declares release 11; these settings should be aligned in future maintenance.

## API configuration

The current implementation reads an `API_KEY` constant in `API.java`; it does not load an environment variable. An embedded credential is present in the repository and should be revoked or rotated by its owner, then replaced with private configuration before further API use.

Do not publish a replacement key in source control. Forecast requests use endpoints that may require a suitable subscription. API responses and account access have not been verified by this documentation update.

## Local files

The file writer creates a `WeatherData/` folder relative to the process working directory. The reader expects the corresponding current, hourly, and daily data files to be available.

## Tests

From `WeatherApp/`:

```sh
mvn test
```

Inspect Maven's reported test count. The API tests make live requests during setup and depend on valid credentials, endpoint access, and locale-sensitive number formatting. They are not isolated offline tests, and no passing result is claimed here.

## Next development steps

- Connect search and forecast controls to the API layer.
- Move credentials out of source code.
- Align Java compiler settings.
- Add mocked API tests and consistent error feedback.
- Make file-loading behavior resilient to missing data.

## Attribution

This is a group coursework repository originating from Tampere University's Programming 3 project template. Source-level author notices identify contributions; this README does not attribute all group code to one person.
