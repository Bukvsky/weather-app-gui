/**
 *
 *  @author Bukowski Igor S31489
 *
 */

package zad1;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class Service {
    private String kraj;
    private String nbp = "https://api.nbp.pl/api/exchangerates/rates/B/VES/";
    private static String apiWeatherKey = "c2e4c31e592a2f962d9cff2d386a0a6a";
    private static String apiExchangeRateKey = "edb2ce30e63af27302cb2730";
    private String currency;
    private String rateFor;
    private String countryCode;
    private Map<String, Double> currencyMap;
    private Map<String, Double> currencyMapToPLN;

    public Service(String kraj) {
        this.kraj = kraj;
        String[] info = getCurrencyOfACountry(kraj);
        this.currency = info[0];
        this.countryCode = info[1];
    }

    public String getWeather(String miasto) {
        String site = "https://api.openweathermap.org/data/2.5/weather?q=" + miasto +","+countryCode+ "&appid=" + apiWeatherKey;
        try (
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new URI(site).toURL().openStream()
                        )
                );
        ) {
            String json = br.lines().collect(Collectors.joining());
            //System.out.println(json);
            return json;

            //JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
            //System.out.println(jsonObject.getAsJsonObject("main").get("temp"));

            //Weather weather = new Gson().fromJson(json, Weather.class);
            //return weather.toString();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Double getRateFor(String kod_waluty) {

        if(kod_waluty.equals(currency)){
            return 1.0;
        }


        this.rateFor = kod_waluty;
        String site = "https://v6.exchangerate-api.com/v6/" + apiExchangeRateKey + "/latest/" + kod_waluty;
        try (
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new URI(site).toURL().openStream()
                        )
                );
        ) {
            String[] json = br.lines().collect(Collectors.toList()).toArray(new String[0]);
            currencyMap = mapOfCurrencies(json);
            return currencyMap.get(currency);

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Double> mapOfCurrencies(String[] data) {
        String[] subarray = Arrays.copyOfRange(data, 10, data.length - 2);
        Map<String, Double> result = new HashMap<>();
        for (String element : subarray) {
            //.println(element);
            String[] parts = element.split(":");
            String code = parts[0].replace("\"", "").trim();
            double val = Double.parseDouble(parts[1].replaceAll("[^\\d.]", ""));

            result.put(code, val);

        }
        return result;
    }

    public Map<String, String> parseWeatherData(String jsonString) {
        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        Map<String, String> weatherData = new HashMap<>();

        String city = jsonObject.get("name").getAsString();
        String country = jsonObject.getAsJsonObject("sys").get("country").getAsString();

        double temp = jsonObject.getAsJsonObject("main").get("temp").getAsDouble() - 273.15;
        double feelsLike = jsonObject.getAsJsonObject("main").get("feels_like").getAsDouble() - 273.15;
        double tempMin = jsonObject.getAsJsonObject("main").get("temp_min").getAsDouble() - 273.15;
        double tempMax = jsonObject.getAsJsonObject("main").get("temp_max").getAsDouble() - 273.15;

        int pressure = jsonObject.getAsJsonObject("main").get("pressure").getAsInt();
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

        int visibility = jsonObject.get("visibility").getAsInt();

        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

        int cloudiness = jsonObject.getAsJsonObject("clouds").get("all").getAsInt();

        JsonArray weatherArray = jsonObject.getAsJsonArray("weather");
        String weatherDescription = weatherArray.get(0).getAsJsonObject().get("description").getAsString();

        long sunrise = jsonObject.getAsJsonObject("sys").get("sunrise").getAsLong();
        long sunset = jsonObject.getAsJsonObject("sys").get("sunset").getAsLong();

        weatherData.put("Lokalizacja", city + ", " + country);
        weatherData.put("Temperatura", String.format("%.2f", temp) + " °C");
        weatherData.put("Odczuwalna temperatura", String.format("%.2f", feelsLike) + " °C");
        weatherData.put("Minimalna temperatura", String.format("%.2f", tempMin) + " °C");
        weatherData.put("Maksymalna temperatura", String.format("%.2f", tempMax) + " °C");
        weatherData.put("Ciśnienie", pressure + " hPa");
        weatherData.put("Wilgotność", humidity + " %");
        weatherData.put("Widoczność", visibility + " m");
        weatherData.put("Wiatr", windSpeed + " m/s");
        weatherData.put("Zachmurzenie", cloudiness + " %");
        weatherData.put("Pogoda", weatherDescription);
        weatherData.put("Wschód słońca", convertUnixTimestampToTime(sunrise));
        weatherData.put("Zachód słońca", convertUnixTimestampToTime(sunset));

        return weatherData;
    }
    private String convertUnixTimestampToTime(long timestamp) {
        long totalSeconds = timestamp;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String[] getCurrencyOfACountry(String targetCountry) {
        Map<String, String> countryCurrencyMap = new HashMap<>();
        String targetCurrencyCode = null;
        String cc = "";
        String[] res = new String[2];
        for (Locale availableLocale : Locale.getAvailableLocales()) {
            String al = availableLocale.toString();
            try {
                Currency currency = Currency.getInstance(availableLocale);
                String countryNameInEnglish = availableLocale.getDisplayCountry(Locale.ENGLISH);

                if (!countryNameInEnglish.isEmpty()) {
                    countryCurrencyMap.put(countryNameInEnglish, currency.getCurrencyCode());
                }
                if (countryNameInEnglish.equalsIgnoreCase(targetCountry)) {
                    targetCurrencyCode = currency.getCurrencyCode();
                    cc=al.substring(al.length()-2);
                }
            } catch (IllegalArgumentException e) {
                continue;
            }
        }
        if (targetCurrencyCode != null) {
            return new String[]{targetCurrencyCode,cc};
        }
        return new String[]{};
    }

    public void setKraj(String kraj) {
        this.kraj = kraj;
    }



    public Double getNBPRate() {
        String[] abc = {"A","B","C"};
        if(currency.equals("PLN")){
            return 1.0;
        }
        for(String sign: abc){
            String site = "https://api.nbp.pl/api/exchangerates/tables/"+sign+"/";
            try (
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(
                                    new URI(site).toURL().openStream()
                            )
                    );
            ) {
                String json = br.lines().collect(Collectors.joining());

                currencyMapToPLN = parseCurrencyRatesWithGson(json);
                if(currencyMapToPLN.containsKey(currency)){
                    return currencyMapToPLN.get(currency);
                }

            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }}
            return 1.0;

    }
    public static Map<String, Double> parseCurrencyRatesWithGson(String json) {
        Map<String, Double> currencyMap = new HashMap<>();
        Gson gson = new Gson();

        try {
            JsonElement jsonElement = JsonParser.parseString(json);
            List<JsonObject> tables = gson.fromJson(jsonElement, new TypeToken<List<JsonObject>>() {}.getType());

            for (JsonObject table : tables) {
                if (table.has("rates")) {
                    List<JsonObject> rates = gson.fromJson(table.get("rates"), new TypeToken<List<JsonObject>>() {}.getType());

                    for (JsonObject rate : rates) {
                        String code = rate.get("code").getAsString();
                        double mid = rate.get("mid").getAsDouble();
                        currencyMap.put(code, mid);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing currency rates", e);
        }

        return currencyMap;
    }


    public String getKraj() {
        return kraj;
    }

    public String getCurrency() {
        return currency;
    }

    public void setRateFor(String rateFor) {
        this.rateFor = rateFor;
    }

    public String getRate() {
        return rateFor;
    }

    public void setCountryInfo(String country){
        this.kraj = country;
        String[] info = getCurrencyOfACountry(country);
        this.currency = info[0];
        this.countryCode = info[1];
    }

}
