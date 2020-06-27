package TestVagrant.WeatherReportingComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TC_001_CompareWeather {
	public WebDriver driver;

	@BeforeTest
	public void setUp() {
		System.setProperty("webdriver.chrome.driver", ".\\driver\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.get("https://www.ndtv.com/");
		driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
	}

	@Test
	public void CheckWeather() {
		driver.findElement(By.className("notnow")).click();
		driver.findElement(By.id("h_sub_menu")).click();
		driver.findElement(By.xpath("//a[text()='WEATHER']")).click();
		driver.findElement(By.xpath("//div[@title='Bengaluru']")).click();
		Assert.assertEquals(driver.findElement(By.xpath("//span[contains(text(),'Bengaluru, Karnataka')]")).getText(),
				"Bengaluru, Karnataka");
		List<WebElement> element = driver
				.findElements(By.xpath("//span[contains(text(),'Bengaluru, Karnataka')]/../..//span//b"));
		ArrayList<String> al = new ArrayList<String>();
		for(WebElement ele : element)
		{
			al.add(ele.getText());
		}
		System.out.println(al.toString());
		System.out.println("Pass");
	}

	@AfterTest
	public void TearDown() {
		driver.quit();
	}
}
