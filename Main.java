/**
 *
 *  @author Bukowski Igor S31489
 *
 */

package zad1;



public class Main {
  public static void main(String[] args) {
      Service s = new Service("Poland");
      String weatherJson = s.getWeather("Warsaw");
      Double rate1 = s.getRateFor("USD");
      Double rate2 = s.getNBPRate();

      GUI gui = new GUI(s.parseWeatherData(weatherJson),rate1,rate2,s);
      gui.launchGui();
  }
}
