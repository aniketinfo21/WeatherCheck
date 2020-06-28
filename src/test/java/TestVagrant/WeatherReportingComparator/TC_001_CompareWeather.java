package TestVagrant.WeatherReportingComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class TC_001_CompareWeather {
	public WebDriver driver;
	public RequestSpecification httpRequest;
	public Response response;
	public ExtentHtmlReporter reporter = new ExtentHtmlReporter("./Reports/Weather.html");
	public ExtentReports extent = new ExtentReports();
	public ExtentTest logger;

	@BeforeTest
	public void setUp() {
		extent.attachReporter(reporter);
		logger = extent.createTest("Weather Comparator");
		System.setProperty("webdriver.chrome.driver", ".\\driver\\chromedriver.exe");
		logger.log(Status.INFO, "Launching chrome browser");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.get("https://www.ndtv.com/");
		logger.log(Status.INFO, "Navigating to NDTV");
		driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		RestAssured.baseURI = "https://api.openweathermap.org/data/2.5/weather";
		httpRequest = RestAssured.given();
	}

	@Test
	public void CheckWeather() {
		driver.findElement(By.className("notnow")).click();
		logger.log(Status.INFO, "Popup handled and selected 'No Thanks'");
		driver.findElement(By.id("h_sub_menu")).click();
		driver.findElement(By.xpath("//a[text()='WEATHER']")).click();
		logger.log(Status.INFO, "Weather Button clicked");
		driver.findElement(By.xpath("//div[@title='Bengaluru']")).click();
		logger.log(Status.INFO, "Selected City Bengaluru");
		Assert.assertEquals(driver.findElement(By.xpath("//span[contains(text(),'Bengaluru, Karnataka')]")).getText(),
				"Bengaluru, Karnataka");
		logger.log(Status.PASS, "Selected City from pin your city is Bengaluru");
		List<WebElement> element = driver
				.findElements(By.xpath("//span[contains(text(),'Bengaluru, Karnataka')]/../..//span//b"));
		ArrayList<String> al = new ArrayList<String>();
		for (WebElement ele : element) {
			al.add(ele.getText());
		}
		NDTVObject obj1 = new NDTVObject();
		obj1.setCondition(al.get(0).toString().split(":")[1].substring(1));
		logger.log(Status.INFO, "Weather from screen Condition : " + al.get(0).toString().split(":")[1].substring(1));
		obj1.setWind(al.get(1).split(":")[1].substring(1).split(" ")[0]);
		logger.log(Status.INFO,
				"Weather from screen Wind speed : " + al.get(1).split(":")[1].substring(1).split(" ")[0]);
		obj1.setHumidity(al.get(2).split(":")[1].substring(1).replace("%", ""));
		logger.log(Status.INFO,
				"Weather from screen  humidity " + al.get(2).split(":")[1].substring(1).replace("%", ""));
		obj1.setTempInDegree(al.get(3).split(":")[1].substring(1));
		logger.log(Status.INFO, "Weather from screen Temp. in Celsius : " + al.get(3).split(":")[1].substring(1));
		obj1.setTempInFahrenheit(al.get(4).split(":")[1].substring(1));
		logger.log(Status.INFO, "Weather from screen Temp. in Fahrenheit : " + al.get(4).split(":")[1].substring(1));
		Assert.assertEquals(obj1.isEmpty(), false);

		response = httpRequest.log().all().queryParam("appid", "7fe67bf08c80ded756e598d6f8fedaea")
				.queryParam("q", "Bengaluru").contentType("application/json").post();
		logger.log(Status.INFO, "Fetching weather report through API api.openweathermap.org");

		APIObject obj2 = new APIObject();
		obj2.setCondition(response.jsonPath().getString("weather[0].main"));
		logger.log(Status.INFO, "Weather from API Condition : "+response.jsonPath().getString("weather[0].main"));
		obj2.setWind(response.jsonPath().getString("wind.speed").toString());
		logger.log(Status.INFO, "Weather from API Wind : "+response.jsonPath().getString("wind.speed").toString());
		obj2.setHumidity(response.jsonPath().getString("main.humidity".toString()));
		logger.log(Status.INFO, "Weather from API Humidity : "+response.jsonPath().getString("main.humidity".toString()));
		obj2.setTempInDegree(Integer.toString(
				(int) Math.round(Double.parseDouble(response.jsonPath().getString("main.temp".toString())) - 273.15)));
		logger.log(Status.INFO, "Weather from API Temp. in Celsius : "+Integer.toString(
				(int) Math.round(Double.parseDouble(response.jsonPath().getString("main.temp".toString())) - 273.15)));
		obj2.setTempInFahrenheit(Integer.toString(
				(int) Math.round((Double.parseDouble(response.jsonPath().getString("main.temp".toString())) - 273.15)*33.8)));
		logger.log(Status.INFO, "Weather from API Temp in Fahrenheit :  "+Integer.toString(
				(int) Math.round((Double.parseDouble(response.jsonPath().getString("main.temp".toString())) - 273.15)*33.8)));
		Assert.assertEquals(obj2.isEmpty(), false);
		if (obj1.equals(obj2)) {
			logger.log(Status.PASS, "Weather report from NDTV and API is same");
		} else {
			logger.log(Status.FAIL, "Weather report from NDTV and API is not same");
		}
	}

	@AfterTest
	public void TearDown() {
		driver.quit();
		extent.flush();
	}
}
