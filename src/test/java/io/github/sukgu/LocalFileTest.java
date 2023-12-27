package io.github.sukgu;

import static java.lang.System.err;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class LocalFileTest {

	//private static boolean isCIBuild = checkEnvironment();
	private static boolean isCIBuild = true;
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
		String url = getPageContent("index.html");
		driver.get(url);
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
	public void testFindElementsWithIncorrectSelector() {
		String url = getPageContent("index.html");
		driver.get(url);
		WebElement element = shadow.findElement("#container");
		List<WebElement> elements = shadow.findElements(element, "#inside1111111");
		assertTrue(elements.size() == 0);
	}
	
	@Test
	public void testFindElementsWithIncorrectSelector2Levels() {
		String url = getPageContent("index.html");
		driver.get(url);
		List<WebElement> elements = shadow.findElements("#container>#inside1111111");
		assertTrue(elements.size() == 0);
	}

	@Test
	public void test2() {
		driver.navigate().to(getPageContent("button.html"));
		WebElement element = shadow.findElement("button");
		shadow.scrollTo(element);
		assertThat(element, notNullValue());
		element.click();
		WebElement p_element = shadow.findElement("div#divid>div#node>p");
		err.println("outerHTML: " + shadow.getAttribute(p_element, "outerHTML"));
		List<WebElement> elements = shadow.getChildElements(element);
		assertThat(elements, notNullValue());
		err.println(elements);
	}

	@Test
	public void test3() {
		driver.navigate().to(getPageContent("button.html"));
		WebElement element = shadow.findElement("button");
		shadow.scrollTo(element);
		assertThat(element, notNullValue());
		element.click();
		WebElement p_element = shadow.findElement("body>div#divid>div#node>p");
		WebElement body = shadow.findElement("body");
		err.println("outerHTML: " + shadow.getAttribute(p_element, "outerHTML"));
		List<WebElement> elements = shadow.getChildElements(body);
		assertThat(elements, notNullValue());
		err.println(elements);

		element = (WebElement)elements.get(0);
		err.println(element);
	}
	
	@Test
	public void testNoSuchElementException() {
		driver.navigate().to(getPageContent("button.html"));
		WebElement element = shadow.findElement("button");
		shadow.scrollTo(element);
		assertThat(element, notNullValue());
		element.click();
		assertThrows(NoSuchElementException.class, () -> {
			shadow.findElement("body>div#divid>div#node>p11");
		});
	}

	@Test
	public void test4() {
		driver.navigate().to(getPageContent("button.html"));
		WebElement parent = shadow.findElement("body");
		assertThat(parent, notNullValue());
		WebElement element = shadow.getShadowElement(parent, "button");
		assertThat(element, notNullValue());
	}
	
	@Test
	public void testImpicitWaitCSS() {
		driver.navigate().to(getPageContent("button.html"));
		WebElement element = shadow.findElement("button");
		shadow.scrollTo(element);
		assertThat(element, notNullValue());
		element.click();
		shadow.setImplicitWait(2);
		WebElement p_element = shadow.findElement("div#divid>div#node>p");
		err.println("outerHTML: " + shadow.getAttribute(p_element, "outerHTML"));
		List<WebElement> elements = shadow.getChildElements(element);
		assertThat(elements, notNullValue());
		err.println(elements);
		shadow.setImplicitWait(0);
	}
	
	@Test
	public void testExplicitWaitCSS() throws Exception {
		driver.navigate().to(getPageContent("button.html"));
		WebElement element = shadow.findElement("button");
		shadow.scrollTo(element);
		assertThat(element, notNullValue());
		element.click();
		shadow.setExplicitWait(5, 1);
		WebElement p_element = shadow.findElement("div#divid>div#node>p");
		err.println("outerHTML: " + shadow.getAttribute(p_element, "outerHTML"));
		List<WebElement> elements = shadow.getChildElements(element);
		assertThat(elements, notNullValue());
		err.println(elements);
		shadow.setExplicitWait(0, 0);
	}
	
	@Test
	public void testImplicitAndExplicitWaitCSS() throws Exception {
		driver.navigate().to(getPageContent("button.html"));
		shadow.setImplicitWait(2);
		WebElement element = shadow.findElement("button");
		shadow.scrollTo(element);
		assertThat(element, notNullValue());
		element.click();
		shadow.setExplicitWait(5, 1);
		WebElement p_element = shadow.findElement("div#divid>div#node>p");
		err.println("outerHTML: " + shadow.getAttribute(p_element, "outerHTML"));
		List<WebElement> elements = shadow.getChildElements(element);
		assertThat(elements, notNullValue());
		err.println(elements);
		shadow.setImplicitWait(0);
		shadow.setExplicitWait(0, 0);
	}
	
	@Test
	public void testImpicitWaitXPath() {
		driver.navigate().to(getPageContent("button.html"));
		WebElement element = shadow.findElementByXPath("//button");
		shadow.scrollTo(element);
		assertThat(element, notNullValue());
		element.click();
		shadow.setImplicitWait(2);
		WebElement p_element = shadow.findElementByXPath("//div[@id='divid']//div[@id='node']//p");
		err.println("outerHTML: " + shadow.getAttribute(p_element, "outerHTML"));
		List<WebElement> elements = shadow.getChildElements(element);
		assertThat(elements, notNullValue());
		err.println(elements);
		shadow.setImplicitWait(0);
	}
	
	@Test
	public void testExplicitWaitXPath() throws Exception {
		driver.navigate().to(getPageContent("button.html"));
		WebElement element = shadow.findElementByXPath("//button");
		shadow.scrollTo(element);
		assertThat(element, notNullValue());
		element.click();
		shadow.setExplicitWait(5, 1);
		WebElement p_element = shadow.findElementByXPath("//div[@id='divid']//div[@id='node']//p");
		err.println("outerHTML: " + shadow.getAttribute(p_element, "outerHTML"));
		List<WebElement> elements = shadow.getChildElements(element);
		assertThat(elements, notNullValue());
		err.println(elements);
		shadow.setExplicitWait(0, 0);
	}
	
	@Test
	public void testImplicitAndExplicitWaitXPath() throws Exception {
		driver.navigate().to(getPageContent("button.html"));
		shadow.setImplicitWait(2);
		WebElement element = shadow.findElementByXPath("//button");
		shadow.scrollTo(element);
		assertThat(element, notNullValue());
		element.click();
		shadow.setExplicitWait(5, 1);
		WebElement p_element = shadow.findElementByXPath("//div[@id='divid']//div[@id='node']//p");
		err.println("outerHTML: " + shadow.getAttribute(p_element, "outerHTML"));
		List<WebElement> elements = shadow.getChildElements(element);
		assertThat(elements, notNullValue());
		err.println(elements);
		shadow.setImplicitWait(0);
		shadow.setExplicitWait(0, 0);
	}
	
	@Test
	public void testXPath() {
		driver.navigate().to(getPageContent("index.html"));
		WebElement element = shadow.findElementByXPath("//body");
		assertThat(element, notNullValue());
	}
	
	@Test
	public void testXPathElementWithParent() {
		driver.navigate().to(getPageContent("button.html"));
		WebElement element = shadow.findElementByXPath("//button");
		shadow.scrollTo(element);
		element.click();
		WebElement element1 = shadow.findElementByXPath("//div[@id='divid']");
		WebElement p_element = shadow.findElementByXPath(element1, "//div[@id='node']//p");
		assertThat(p_element, notNullValue());
	}
	
	@Test
	public void testXPathElementsWithParent() {
		driver.navigate().to(getPageContent("button.html"));
		WebElement element = shadow.findElementByXPath("//button");
		shadow.scrollTo(element);
		element.click();
		WebElement element1 = shadow.findElementByXPath("//div[@id='divid']");
		List<WebElement> p_element = shadow.findElementsByXPath(element1, "//div[@id='node']//p");
		assertThat(p_element.get(0), notNullValue());
	}
	
	@Test
	public void testXPathWithIndex() {
		driver.navigate().to(getPageContent("index.html"));
		WebElement element = shadow.findElementByXPath("//body//div[1]");
		assertThat(element, notNullValue());
	}
	
	@Test
	public void testXPathWithText() {
		driver.navigate().to(getPageContent("index.html"));
		WebElement element = shadow.findElementByXPath("//h3[text()='some DOM element']");
		assertThat(element, notNullValue());
	}
	
	@Test
	public void testXPathWithTextInsideShadow() {
		driver.navigate().to(getPageContent("index.html"));
		WebElement element = shadow.findElementByXPath("//div[@id='container']//h2[text()='Inside Shadow DOM']");
		assertThat(element, notNullValue());
	}
	
	@Test
	public void testAllElementsXPath() {
		driver.navigate().to(getPageContent("index.html"));
		WebElement element = shadow.findElementByXPath("//div[@id='container']//h2[text()='Inside Shadow DOM']");
		assertThat(element, notNullValue());
	}
	
	@Test
	public void testXPathWithPipe() {
		driver.navigate().to(getPageContent("index.html"));
		WebElement element = shadow.findElementByXPath("//div[@id='container']//h2[text()='Inside Shadow DOM1'] | //div[@id='container']//h2[text()='Inside Shadow DOM']");
		assertThat(element, notNullValue());
	}
	
	@Test
	public void testAllElementsXPathWithText() {
		driver.navigate().to(getPageContent("index.html"));
		List<WebElement> element = shadow.findElementsByXPath("//div[@id='container']//h2[text()='Inside Shadow DOM']");
		assert element.size()==2;
	}
	
	@Test
	public void testAllElementsXPathWithId() {
		driver.navigate().to(getPageContent("index.html"));
		List<WebElement> element = shadow.findElementsByXPath("//div[@id='container']//h2[@id='inside']");
		assert element.size()==2;
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