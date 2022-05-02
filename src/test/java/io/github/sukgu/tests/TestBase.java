package io.github.sukgu.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.sukgu.Shadow;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static io.github.sukgu.config.Properties.browser;
import static java.lang.System.err;

public abstract class TestBase {
    protected static Shadow shadow = null;
    protected static WebDriver driver = null;

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
        } // TODO: finish for other browser
        shadow = new Shadow(driver);
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }
}
