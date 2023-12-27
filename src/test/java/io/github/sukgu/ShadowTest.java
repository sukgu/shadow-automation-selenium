package io.github.sukgu;

import static java.lang.System.err;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ShadowTest {

	private final static String baseUrl = "https://www.virustotal.com";
	// private static final String urlLocator = "a[data-route='url']";
	private static final String urlLocator = "home-view a[data-route='url']";
	private static final String pageHeading = "home-view div.container";
	private static final boolean debug = Boolean
			.parseBoolean(getPropertyEnv("DEBUG", "false"));;
	private static WebDriver driver = null;
	private static Shadow shadow = null;
	private static String browser = getPropertyEnv("BROWSER",
			getPropertyEnv("webdriver.driver", "chrome"));
	@BeforeAll
	public static void injectShadowJS() {
		err.println("Launching " + browser);
		if (browser.equals("chrome")) {
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
		}
		if (browser.equals("firefox")) {
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
		} // TODO: finish for other browsers
		driver.navigate().to(baseUrl);
		shadow = new Shadow(driver);
	}

	@BeforeEach
	public void init() {

	}

	@Test
	public void testHighlightWithGrennColor() {
		WebElement element = shadow.findElement(urlLocator);
		shadow.highlight(element, "green", 3000);
	}
	
	@Test
	public void testHighlightWithDefaultColor() {
		WebElement element = shadow.findElement(urlLocator);
		shadow.highlight(element);
	}

	@Test
	public void testJSInjection() {
		WebElement element = shadow.findElement(urlLocator);
		err.println(element);
		Assertions.assertEquals(new String("URL"), element.getText(), "Not Matched");
	}

	@Test
	public void testGetAllObject() {
		List<WebElement> elements = shadow.findElements(urlLocator);
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
		err.println(String.format("Found %d elements:", elements.size()));
		/* elements.stream().forEach(err::println);
		 elements.stream().map(o -> o.getTagName()).forEach(err::println);
		elements.stream()
				.map(o -> String.format("innerHTML: %s", o.getAttribute("innerHTML")))
				.forEach(err::println);
				*/
		elements.stream()
				.map(o -> String.format("outerHTML: %s", o.getAttribute("outerHTML")))
				.forEach(err::println);
	}

	@Test
	public void testAPICalls1() {
		WebElement element = shadow.findElements(urlLocator).get(0);

		WebElement element1 = shadow.getParentElement(element);
		WebElement element2 = shadow.getNextSiblingElement(element1);
		assertThat(element2, notNullValue());
		// TODO: compare siblings
	}

	@Test
	public void testAPICalls2() {
		WebElement element = shadow.findElements(pageHeading).get(0);
		List<WebElement> elements = shadow.findElements(element, "p");
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
	}

	@Test
	public void testAPICalls3() {
		WebElement element = shadow.findElement(urlLocator);
		List<WebElement> elements = shadow.getSiblingElements(element);
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
	}

	@Test
	public void testAPICalls4() {
		WebElement element = shadow.findElement(pageHeading);
		List<WebElement> elements = shadow.getChildElements(element);
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
	}

	@Test
	public void testAPICalls5() {
		List<WebElement> elements = shadow
				.findElements(shadow.findElement(pageHeading), ".omnibar");
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
		err.println(String.format("Found %d elements: ", elements.size()));
		elements.stream()
				.map(o -> String.format("outerHTML: %s", o.getAttribute("outerHTML")))
				.forEach(err::println);
	}

	@AfterEach
	public void tearDown() {
	}

	@AfterAll
	public static void tearDownAll() {
		driver.quit();
	}

	public static String getPropertyEnv(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (debug) {
			err.println("system property " + name + " = " + value);
		}
		if (value == null || value.length() == 0) {
			value = System.getenv(name);
			if (debug) {
				err.println("system env " + name + " = " + value);
			}
			if (value == null || value.length() == 0) {
				value = defaultValue;
				if (debug) {
					err.println("default value  = " + value);
				}
			}
		}
		return value;
	}

	public static boolean checkEnvironment() {
		Map<String, String> env = System.getenv();
		boolean result = false;
		if (env.containsKey("TRAVIS") && env.get("TRAVIS").equals("true")) {
			result = true;
		}
		return result;
	}

}