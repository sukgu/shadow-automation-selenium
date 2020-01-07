package io.github.sukgu;

import static java.lang.System.err;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class LocalFileTest {

	private static boolean isCIBuild = checkEnvironment();

	private static final boolean debug = Boolean
			.parseBoolean(getPropertyEnv("DEBUG", "false"));

	private static WebDriver driver = null;
	private static Shadow shadow = null;
	private static String browser = getPropertyEnv("BROWSER",
			getPropertyEnv("webdriver.driver", "chrome"));
	// export BROWSER=firefox or
	// use -Pfirefox to override
	@SuppressWarnings("unused")
	private static final boolean headless = Boolean
			.parseBoolean(getPropertyEnv("HEADLESS", "false"));

	@BeforeAll
	public static void injectShadowJS() {
		err.println("Launching " + browser);
		if (isCIBuild) {
			if (browser.equals("chrome")) {
				WebDriverManager.chromedriver().setup();
				driver = new ChromeDriver();
			}
			if (browser.equals("firefox")) {
				WebDriverManager.firefoxdriver().setup();
				driver = new FirefoxDriver();
			} // TODO: finish for other browser
		}
		shadow = new Shadow(driver);
	}

	@AfterEach
	public void AfterMethod() {
		driver.get("about:blank");
	}

	@Test
	public void test1() {
		driver.navigate().to(getPageContent("index.html"));
		WebElement element = shadow.findElement("#container");
		List<WebElement> elements = shadow.getAllShadowElement(element, "#inside");
		assertThat(elements, notNullValue());
		assertThat(elements.size(), greaterThan(0));
		err.println(
				String.format("Located %d shadow document elements:", elements.size()));
		elements.stream()
				.map(o -> String.format("outerHTML: %s", o.getAttribute("outerHTML")))
				.forEach(err::println);
	}

	@Test
	public void test2() {
		driver.navigate().to(getPageContent("button.html"));
		WebElement element = shadow.findElement("button");
		shadow.scrollTo(element);
		assertThat(element, notNullValue());
		err.println("outerTML: " + shadow.getAttribute(element, "outerHTML"));
		List<WebElement> elements = shadow.getChildElements(element);
		assertThat(elements, notNullValue());
		err.println(elements);
	}

	@Disabled("Disabled until com.google.common.collect.Maps$TransformedEntriesMap cannot be cast to org.openqa.selenium.WebElement is addressed")
	@Test
	public void test3() {
		driver.navigate().to(getPageContent("button.html"));
		WebElement element = shadow.findElement("button");
		shadow.scrollTo(element);
		assertThat(element, notNullValue());
		err.println("outerTML: " + shadow.getAttribute(element, "outerHTML"));
		List<WebElement> elements = shadow.getChildElements(element);
		assertThat(elements, notNullValue());
		err.println(elements);

		element = elements.get(0);
		err.println(element);
	}

	@Disabled("Disabled until getShadowElement javascript error: Cannot read property 'querySelector' of null is addressed")
	@Test
	public void test4() {

		driver.navigate().to(getPageContent("button.html"));
		WebElement parent = shadow.findElement("body");
		assertThat(parent, notNullValue());
		// Cannot read property 'querySelector' of null
		WebElement element = shadow.getShadowElement(parent, "button");
		assertThat(element, notNullValue());
	}

	@AfterAll
	public static void tearDownAll() {
		driver.close();
	}

	// Utilities
	public static String getOSName() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.startsWith("windows")) {
			osName = "windows";
		}
		return osName;
	}

	// origin:
	// https://github.com/TsvetomirSlavov/wdci/blob/master/code/src/main/java/com/seleniumsimplified/webdriver/manager/EnvironmentPropertyReader.java
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

	protected static String getPageContent(String pagename) {
		try {
			URI uri = LocalFileTest.class.getClassLoader().getResource(pagename)
					.toURI();
			err.println("Testing local file: " + uri.toString());
			return uri.toString();
		} catch (URISyntaxException e) { // NOTE: multi-catch statement is not
			// supported in -source 1.6
			throw new RuntimeException(e);
		}
	}
}
