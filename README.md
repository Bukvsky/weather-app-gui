# Network Services Client

## Overview  
This project is a Java application with a graphical user interface (GUI) that provides the following functionalities based on the city and country name entered by the user:  

- **Current Weather Information** – Retrieves real-time weather data for the specified city.  
- **Exchange Rate Information** – Displays the exchange rate of the country's currency against a user-specified currency.  
- **NBP Exchange Rate** – Shows the exchange rate of the Polish złoty (PLN) against the country's currency using data from the National Bank of Poland (NBP).  
- **Wikipedia Page** – Embeds a browser displaying the Wikipedia page of the specified city.  

## Features  
- Uses **[OpenWeather API](https://api.openweathermap.org)** to fetch weather data.  
- Retrieves exchange rates from **[ExchangeRate-API](https://www.exchangerate-api.com/docs/free)**.  
- Fetches exchange rates for PLN from **[NBP](https://nbp.pl/statystyka-i-sprawozdawczosc/kursy/tabela-a/)**.  
- Integrates **JavaFX WebEngine** to display Wikipedia pages within the Swing-based application.  

## Implementation Details  
The core of the project is the `Service` class, which provides the following methods:  

```java
public class Service {
    public Service(String country) { ... }
    
    public String getWeather(String city) { ... }  // Returns weather data in JSON format.
    
    public Double getRateFor(String currencyCode) { ... }  // Returns exchange rate of the country's currency against the given currency.
    
    public Double getNBPRate() { ... }  // Returns the exchange rate of PLN against the country's currency.
}
```

### Example Usage  
```java
public class Main {
    public static void main(String[] args) {
        Service s = new Service("Poland");
        String weatherJson = s.getWeather("Warsaw");
        Double rate1 = s.getRateFor("USD");
        Double rate2 = s.getNBPRate();
        
        // GUI initialization...
    }
}
```

## Installation and Setup  
1. Ensure you have Java installed.  
2. Include required JAR dependencies in the `lib` directory.  
3. Configure the **Build Path** to include all required JAR files.  
4. Run the application from the `Main` class.  

## Notes  
- The `Service` class is designed to be independent of the GUI.  
- External libraries (JAR files) must be placed in the project's `lib` directory and properly linked in the Build Path.
- Connection between JavaFX and wikipedia is based on Polish wiki, if you would like to change it, in gui you need to change baseURL in loadWebPage method. For example from : "https://pl.wikipedia.org/wiki/" to "https://en.wikipedia.org/wiki/"


##Some screenshots from the gui
![Zrzut ekranu 2025-03-31 163215](https://github.com/user-attachments/assets/145a3f53-a3bf-4f02-aa47-2658e68f2710)

![Zrzut ekranu 2025-03-31 163230](https://github.com/user-attachments/assets/a9720cd7-28bd-48c9-b83b-c15de7d8d426)

![Zrzut ekranu 2025-03-31 163250](https://github.com/user-attachments/assets/db86d417-6cae-42a7-b726-27c3312263a0)


## License  
This project is licensed under the MIT License. Feel free to use and modify it!
