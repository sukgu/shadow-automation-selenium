package io.github.sukgu;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Arrays;
import java.util.List;

import static java.lang.System.err;

public class ShadowDriverTest {

    private final static String baseUrl = "https://www.virustotal.com";
    private static final boolean debug = Boolean
            .parseBoolean(getPropertyEnv("DEBUG", "false"));
    ;
    private static WebDriver driver = null;
    private static String browser = getPropertyEnv("BROWSER",
            getPropertyEnv("webdriver.driver", "chrome"));

    @Test
    public void driver_shouldFindElement_whenProvideBySelector() {
        List<By> selectors = Arrays.asList(
                By.cssSelector("[data-route=url]"),
                By.name("bare-account"),
                By.xpath("//div[@id='view-container']"),
                By.id("notificationsIcon"),
                By.className("about"),
                By.tagName("body"),
                By.partialLinkText("Vote and Com"),
                By.linkText("API")
        );

        selectors.forEach(by -> {
            WebElement sut = driver.findElement(by);
            err.println(">>" + by + "<< element size: " + sut.getSize());
        });
    }

    @Test
    public void driver_shouldFindElements_whenProvideBySelector() {
        List<By> selectors = Arrays.asList(
                By.cssSelector("[data-route=url]"),
                By.name("trigger"),
                By.xpath("//*[contains(@id,'-feature')]"),
                By.id("wrapperLink"),
                By.className("about"),
                By.tagName("div"),
                By.partialLinkText("API"),
                By.linkText("API")
        );

        selectors.forEach(by -> {
            List<WebElement> sut = driver.findElements(by);
            err.println(">>" + by + "<< element count: " + sut.size());
        });
    }

//    private static Stream<Arguments> findElementBySelectors() {
//        return Stream.of(
//                Arguments.of(By.cssSelector("vt-ui-main-upload-form > infoIcon")),
//                Arguments.of(By.name("form.suspicious-dns")),
//                Arguments.of(By.xpath("//*[@id=\"autocompleteDropdown\"]")),
//                Arguments.of(By.id("searchBarInputDropdown")),
//                Arguments.of(By.className("blue")),
//                Arguments.of(By.tagName("footer")),
//                Arguments.of(By.partialLinkText("Vote and Com")),
//                Arguments.of(By.linkText("Intelligence"))
//        );
//    }

//    private static Stream<Arguments> findElementsBySelectors() {
//        return Stream.of(
//                Arguments.of(By.cssSelector()),
//                Arguments.of(By.name()),
//                Arguments.of(By.xpath()),
//                Arguments.of(By.id()),
//                Arguments.of(By.className()),
//                Arguments.of(By.tagName()),
//                Arguments.of(By.partialLinkText()),
//                Arguments.of(By.linkText())
//        );
//    }

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
        driver.manage().window().maximize();
        driver.navigate().to(baseUrl);
        driver = new ShadowDriver(driver);
    }

    @AfterEach
    public void tearDown() {
    }

    @AfterAll
    public static void tearDownAll() {
        driver.close();
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
}