package io.github.sukgu.tests;

import io.github.sukgu.ShadowDriver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.lang.System.err;

public class ShadowDriverTest extends TestBase {
    private final static String baseUrl = "https://www.virustotal.com";

    @BeforeAll
    public static void injectShadowJS() {
        TestBase.injectShadowJS();
        driver = new ShadowDriver(driver);
        driver.get(baseUrl);
    }

    @ParameterizedTest
    @MethodSource("io.github.sukgu.data.DataProvider#findElementSelectors")
    public void driver_shouldFindElement_whenProvideBySelector(By by) {
        WebElement sut = driver.findElement(by);
        err.println(">>" + by + "<< element tag: " + sut.getAttribute("tagName"));
    }

    @ParameterizedTest
    @MethodSource("io.github.sukgu.data.DataProvider#findElementsSelectors")
    public void driver_shouldFindElements_whenProvideBySelector(By by) {
        List<WebElement> sut = driver.findElements(by);
        err.println(">>" + by + "<< element count: " + sut.size());
        assert !sut.isEmpty();
    }
}