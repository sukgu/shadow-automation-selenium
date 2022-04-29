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
                By.name("trigger"),
                By.xpath("//span[@id='wrapperLink']"),
                By.id("wrapperLink"),
                By.className("about"),
                By.tagName("body"),
                By.partialLinkText("Check our"),
                By.linkText("Check our API")
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
                By.linkText("Check our API")
        );

        selectors.forEach(by -> {
            List<WebElement> sut = driver.findElements(by);
            err.println(">>" + by + "<< element count: " + sut.size());
            assert !sut.isEmpty();
        });
    }

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