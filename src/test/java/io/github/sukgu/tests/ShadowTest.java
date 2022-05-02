package io.github.sukgu.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.lang.System.err;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

public class ShadowTest extends TestBase {
    private final static String baseUrl = "https://www.virustotal.com";
    private static final String urlLocator = "vt-ui-shell vt-ui-button[data-route='url']";

    @BeforeAll
    public static void injectShadowJS() {
        TestBase.injectShadowJS();
        driver.get(baseUrl);
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
        Assertions.assertEquals("URL", element.getText(), "Not Matched");
    }

    @Test
    public void testGetAllObject() {
        List<WebElement> elements = shadow.findElements(urlLocator);
        assertThat(elements, notNullValue());
        assertThat(elements.size(), greaterThan(0));
        err.printf("Found %d elements:%n", elements.size());

        elements.stream()
                .map(o -> String.format("outerHTML: %s", o.getAttribute("outerHTML")))
                .forEach(err::println);
    }

    @Test
    public void testAPICalls1() {
        WebElement element = shadow.findElements(urlLocator).get(0);

        WebElement element1 = shadow.getNextSiblingElement(element);
        assertThat(element1, notNullValue());
        // TODO: compare siblings
    }

    @Test
    public void testAPICalls2() {
        WebElement element = shadow.findElements(urlLocator).get(0);
        List<WebElement> elements = shadow.findElements(element, "div");
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
        WebElement element = shadow.findElement(urlLocator);
        List<WebElement> elements = shadow.getChildElements(element);
        assertThat(elements, notNullValue());
        assertThat(elements.size(), greaterThan(0));
    }

    @Test
    public void testAPICalls5() {
        List<WebElement> elements = shadow
                .findElements(shadow.findElement(urlLocator), "#wrapperLink");
        assertThat(elements, notNullValue());
        assertThat(elements.size(), greaterThan(0));
        err.printf("Found %d elements: %n", elements.size());
        elements.stream()
                .map(o -> String.format("outerHTML: %s", o.getAttribute("outerHTML")))
                .forEach(err::println);
    }
}