package io.github.sukgu;

import static java.lang.System.err;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.sukgu.pom.LocalTestPage;

public class AnnotationTest {

	//private static boolean isCIBuild = checkEnvironment();
	private static boolean isCIBuild = true;
	private static final boolean debug = Boolean
			.parseBoolean(getPropertyEnv("DEBUG", "false"));

	private static WebDriver driver = null;
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
	}

	@Test
	public void test1() {
		String url = getPageContent("index.html");
		driver.get(url);
		LocalTestPage page = new LocalTestPage(driver);
		assertThat(page.getContainer(), notNullValue());
	}
	
	@Test
	public void testFindElementsWithSelector() {
		String url = getPageContent("index.html");
		driver.get(url);
		LocalTestPage page = new LocalTestPage(driver);
		assertTrue(page.getInsides().get(0).getTagName().equals("h2"));
	}
	
	@Test
	public void testFindElementsWithSelector2Levels() {
		String url = getPageContent("index.html");
		driver.get(url);
		LocalTestPage page = new LocalTestPage(driver);
		assertTrue(page.getLevelInsides().get(0).getTagName().equals("h2"));
	}
	
	@Test
	public void testFindByAnnotationWithH3() {
		String url = getPageContent("index.html");
		driver.get(url);
		LocalTestPage page = new LocalTestPage(driver);
		assertTrue(page.getH3().getText().equals("some DOM element"));
	}
	
	@Test
	public void testFindByAnnotationWithAllH3() {
		String url = getPageContent("index.html");
		driver.get(url);
		LocalTestPage page = new LocalTestPage(driver);
		assertTrue(page.getAllH3().get(1).getText().equals("some DOM element"));
	}

	@Test
	public void testGetParagraph() {
		driver.navigate().to(getPageContent("button.html"));
		LocalTestPage page = new LocalTestPage(driver);
		assertThat(page.getButton(), notNullValue());
		page.getButton().click();
		LocalTestPage page1 = new LocalTestPage(driver);
		assertThat(page1.getParagraph(), notNullValue());
	}

	@Test
	public void testGetBody() {
		driver.navigate().to(getPageContent("button.html"));
		LocalTestPage page = new LocalTestPage(driver);
		assertThat(page.getBody(), notNullValue());
	}
	
	@Test
	public void testNoSuchElementException() {
		driver.navigate().to(getPageContent("button.html"));
		LocalTestPage page = new LocalTestPage(driver);
		assertThat(page.getButton(), notNullValue());
		page.getButton().click();
		LocalTestPage page1 = new LocalTestPage(driver);
		page1.getNoSuchElement();
		assertThrows(NoSuchElementException.class, () -> {
			page1.getNoSuchElement().getTagName();
		});
	}
	
	@Test
	public void testXPath() {
		driver.navigate().to(getPageContent("index.html"));
		LocalTestPage page = new LocalTestPage(driver);
		assertThat(page.getBodyByXPath(), notNullValue());
	}
	
	@Test
	public void testXPathWithIndex() {
		driver.navigate().to(getPageContent("index.html"));
		LocalTestPage page = new LocalTestPage(driver);
		assertThat(page.getDivByIndex(), notNullValue());
	}
	
	@Test
	public void testXPathWithText() {
		driver.navigate().to(getPageContent("index.html"));
		LocalTestPage page = new LocalTestPage(driver);
		assertThat(page.getH3ByXPathWithText(), notNullValue());
	}
	
	@Test
	public void testXPathWithTextInsideShadow() {
		driver.navigate().to(getPageContent("index.html"));
		LocalTestPage page = new LocalTestPage(driver);
		assertThat(page.getH3ByXPathWithTextInsideShadow(), notNullValue());
	}
	
	@Test
	public void testXPathWithPipe() {
		driver.navigate().to(getPageContent("index.html"));
		LocalTestPage page = new LocalTestPage(driver);
		assertThat(page.getH2XPathWithPipe(), notNullValue());
	}
	
	@Test
	public void testAllElementsXPathWithText() {
		driver.navigate().to(getPageContent("index.html"));
		LocalTestPage page = new LocalTestPage(driver);
		assert page.getH2AllElementsXPathWithText().get(0).getText().equals("Inside Shadow DOM");
	}
	
	@Test
	public void testAllElementsXPathWithId() {
		driver.navigate().to(getPageContent("index.html"));
		LocalTestPage page = new LocalTestPage(driver);
		assert page.getH2AllElementsXPathWithId().get(0).getTagName().equals("h2");
	}

	@AfterAll
	public static void tearDownAll() {
		driver.quit();
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
			err.println("Testing local file: " + uri.getPath().toString());
			return "file://"+uri.getPath().toString();
		} catch (URISyntaxException e) { // NOTE: multi-catch statement is not
			// supported in -source 1.6
			throw new RuntimeException(e);
		}
	}

}
