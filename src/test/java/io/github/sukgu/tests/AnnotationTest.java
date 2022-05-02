package io.github.sukgu.tests;

import io.github.sukgu.pom.LocalTestPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.System.err;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AnnotationTest extends TestBase {
    protected String getPageContent(String pageName) {
        try {
            URI uri = LocalFileTest.class.getClassLoader().getResource(pageName).toURI();
            err.println("Testing local file: " + uri.getPath());
            return "file://" + uri.getPath();
        } catch (URISyntaxException e) { // NOTE: multi-catch statement is not
            // supported in -source 1.6
            throw new RuntimeException(e);
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
}
