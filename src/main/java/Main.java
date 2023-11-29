import Webscraping.ArknightsScraper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class Main {

  public static void main(String[] args) throws ParserConfigurationException, InterruptedException, TransformerException {
    ArknightsScraper a = new ArknightsScraper();
    a.start();
  }
}
