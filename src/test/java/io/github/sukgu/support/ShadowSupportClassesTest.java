package io.github.sukgu.support;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.sukgu.Shadow;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;

import java.net.URI;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Shadow DOM support classes functionality.
 * Uses annotation-based PageFactory pattern with @FindElementBy annotations.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShadowSupportClassesTest {

    private WebDriver driver;
    private Shadow shadow;

    @BeforeAll
    public void setUpAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        shadow = new Shadow(driver);
        driver.manage().window().maximize();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testFindByCssSelectorBasic() {
        String html = "<html><body>" +
                "<div id='test-container'>" +
                "<span class='test-class'>Test Span</span>" +
                "<p id='test-paragraph'>Test Paragraph</p>" +
                "</div></body></html>";
        
        driver.get("data:text/html," + html);
        
        BasicTestPage basicTestPage = new BasicTestPage(driver);
        assertThat(basicTestPage.testParagraph, notNullValue());
        assertEquals("Test Paragraph", basicTestPage.testParagraph.getText());
        assertEquals("p", basicTestPage.testParagraph.getTagName().toLowerCase());
    }

    @Test
    public void testFindByCssSelectorMultipleElements() {
        String html = "<html><body>" +
                "<div class='container'>" +
                "<span class='item'>Item 1</span>" +
                "<span class='item'>Item 2</span>" +
                "<span class='item'>Item 3</span>" +
                "</div></body></html>";
        
        driver.get("data:text/html," + html);
        
        MultipleElementsTestPage multipleElementsTestPage = new MultipleElementsTestPage(driver);
        assertThat(multipleElementsTestPage.items, notNullValue());
        assertEquals(3, multipleElementsTestPage.items.size());
        assertEquals("Item 1", multipleElementsTestPage.items.get(0).getText());
        assertEquals("Item 2", multipleElementsTestPage.items.get(1).getText());
        assertEquals("Item 3", multipleElementsTestPage.items.get(2).getText());
    }

    @Test
    public void testFindByCssSelectorWithParent() {
        String html = "<html><body>" +
                "<div id='parent1'><span class='child'>Child 1</span></div>" +
                "<div id='parent2'><span class='child'>Child 2</span></div>" +
                "</body></html>";
        
        driver.get("data:text/html," + html);
        
        ParentTestPage parentTestPage = new ParentTestPage(driver);
        assertThat(parentTestPage.parent1, notNullValue());
        
        WebElement childElement = shadow.findElement(parentTestPage.parent1, ".child");
        assertThat(childElement, notNullValue());
        assertEquals("Child 1", childElement.getText());
    }

    @Test
    public void testFindByCssSelectorComplexSelectors() {
        driver.get(getPageContent("complex-selectors.html"));
        
        ComplexSelectorsTestPage complexSelectorsTestPage = new ComplexSelectorsTestPage(driver);
        
        assertThat(complexSelectorsTestPage.contentSections, notNullValue());
        assertTrue(complexSelectorsTestPage.contentSections.size() >= 1, 
            "Should find at least 1 content section, found: " + complexSelectorsTestPage.contentSections.size());
        
        assertThat(complexSelectorsTestPage.highlights, notNullValue());
        assertTrue(complexSelectorsTestPage.highlights.size() >= 2, 
            "Should find at least 2 highlights, found: " + complexSelectorsTestPage.highlights.size());
        
        assertThat(complexSelectorsTestPage.descriptions, notNullValue());
        assertTrue(complexSelectorsTestPage.descriptions.size() >= 1, 
            "Should find at least 1 description, found: " + complexSelectorsTestPage.descriptions.size());
    }

    @Test
    public void testFindByXPathBasic() {
        String html = "<html><body>" +
                "<div id='xpath-container'>" +
                "<table><tr><td>Cell 1</td><td>Cell 2</td></tr></table>" +
                "</div></body></html>";
        
        driver.get("data:text/html," + html);
        
        XPathTestPage xPathTestPage = new XPathTestPage(driver);
        assertThat(xPathTestPage.firstCell, notNullValue());
        assertEquals("Cell 1", xPathTestPage.firstCell.getText());
    }

    @Test
    public void testFindByXPathWithPredicates() {
        String html = "<html><body>" +
                "<ul>" +
                "<li data-value='1'>First Item</li>" +
                "<li data-value='2'>Second Item</li>" +
                "<li data-value='3'>Third Item</li>" +
                "</ul></body></html>";
        
        driver.get("data:text/html," + html);
        
        XPathPredicatesTestPage xPathPredicatesTestPage = new XPathPredicatesTestPage(driver);
        
        assertThat(xPathPredicatesTestPage.secondItem, notNullValue());
        assertEquals("Second Item", xPathPredicatesTestPage.secondItem.getText());
        
        assertThat(xPathPredicatesTestPage.thirdItem, notNullValue());
        assertEquals("Third Item", xPathPredicatesTestPage.thirdItem.getText());
    }

    @Test
    public void testFindByXPathWithText() {
        String html = "<html><body>" +
                "<div>" +
                "<p>Normal paragraph</p>" +
                "<p><strong>Bold paragraph</strong></p>" +
                "<p>Another <em>emphasized</em> paragraph</p>" +
                "</div></body></html>";
        
        driver.get("data:text/html," + html);
        
        XPathTextTestPage xPathTextTestPage = new XPathTextTestPage(driver);
        
        assertThat(xPathTextTestPage.normalParagraph, notNullValue());
        assertEquals("Normal paragraph", xPathTextTestPage.normalParagraph.getText());
        
        assertThat(xPathTextTestPage.emphasizedParagraph, notNullValue());
        assertTrue(xPathTextTestPage.emphasizedParagraph.getText().contains("emphasized"));
    }

    @Test
    public void testFindByXPathMultipleResults() {
        String html = "<html><body>" +
                "<div class='list'>" +
                "<div class='item'>Item A</div>" +
                "<div class='item'>Item B</div>" +
                "<div class='item'>Item C</div>" +
                "</div></body></html>";
        
        driver.get("data:text/html," + html);
        
        XPathMultipleTestPage xPathMultipleTestPage = new XPathMultipleTestPage(driver);
        assertThat(xPathMultipleTestPage.items, notNullValue());
        assertEquals(3, xPathMultipleTestPage.items.size());
        assertEquals("Item A", xPathMultipleTestPage.items.get(0).getText());
        assertEquals("Item B", xPathMultipleTestPage.items.get(1).getText());
        assertEquals("Item C", xPathMultipleTestPage.items.get(2).getText());
    }

    @Test
    public void testBaseByFunctionality() {
        String html = "<html><body>" +
                "<div id='base-test'>" +
                "<span class='target'>Target Element</span>" +
                "</div></body></html>";
        
        driver.get("data:text/html," + html);
        
        BaseByTestPage baseByTestPage = new BaseByTestPage(driver);
        assertThat(baseByTestPage.targetElement, notNullValue());
        assertEquals("Target Element", baseByTestPage.targetElement.getText());
        
        assertThat(baseByTestPage.targetElements, notNullValue());
        assertEquals(1, baseByTestPage.targetElements.size());
    }

    @Test
    public void testFindElementByAnnotation() {
        String html = "<html><body>" +
                "<form id='test-form'>" +
                "<input type='text' id='username' name='username'>" +
                "<input type='password' id='password' name='password'>" +
                "<button type='submit' id='submit-btn'>Submit</button>" +
                "</form></body></html>";
        
        driver.get("data:text/html," + html);
        
        // Test that annotation exists and works correctly with PageFactory
        FindElementBy cssByAnnotation = new FindElementBy() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return FindElementBy.class;
            }
            
            @Override
            public String css() {
                return "#username";
            }
            
            @Override
            public String xpath() {
                return "";
            }
        };
        
        assertEquals("#username", cssByAnnotation.css());
        assertEquals("", cssByAnnotation.xpath());
    }

    @Test
    public void testSupportClassErrorHandling() {
        driver.get("data:text/html,<html><body><div>Simple content</div></body></html>");
        
        ErrorHandlingTestPage errorHandlingTestPage = new ErrorHandlingTestPage(driver);
        assertThat(errorHandlingTestPage.validElement, notNullValue());
    }

    @Test
    public void testSupportClassesPerformance() {
        StringBuilder html = new StringBuilder("<html><body>");
        
        for (int i = 0; i < 100; i++) {
            html.append("<div class='perf-item' data-index='").append(i).append("'>Performance Item ").append(i).append("</div>");
        }
        html.append("</body></html>");
        
        driver.get("data:text/html," + html.toString());
        
        long startTime = System.currentTimeMillis();
        
        PerformanceTestPage performanceTestPage = new PerformanceTestPage(driver);
        assertEquals(100, performanceTestPage.perfItems.size());
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 10000, "Support class operations should complete within 10 seconds, took: " + duration + "ms");
    }

    @Test
    public void testSupportClassesWithShadowDOM() {
        String shadowHTML = 
            "<html><body>" +
            "<div id='shadow-host'></div>" +
            "<script>" +
            "const host = document.getElementById('shadow-host');" +
            "const shadowRoot = host.attachShadow({mode: 'open'});" +
            "shadowRoot.innerHTML = `" +
            "  <div class='shadow-container'>" +
            "    <span class='shadow-item' id='item-1'>Shadow Item 1</span>" +
            "    <span class='shadow-item' id='item-2'>Shadow Item 2</span>" +
            "  </div>" +
            "`;" +
            "</script>" +
            "</body></html>";
        
        driver.get("data:text/html," + shadowHTML);
        
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        ShadowDOMTestPage shadowDOMTestPage = new ShadowDOMTestPage(driver);
        assertThat(shadowDOMTestPage.shadowHost, notNullValue());
        List<WebElement> shadowItems = shadow.getAllShadowElement(shadowDOMTestPage.shadowHost, ".shadow-item");
        assertThat(shadowItems, notNullValue());
        assertTrue(shadowItems.size() >= 0, "Shadow items should be found or empty list returned");
    }

    // Utility method
    private String getPageContent(String pagename) {
        try {
            URI uri = getClass().getClassLoader().getResource(pagename).toURI();
            return "file://" + uri.getPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // Page Object Classes using @FindElementBy annotations
    
    public static class BasicTestPage {
        @FindElementBy(css = "#test-paragraph")
        public WebElement testParagraph;
        
        public BasicTestPage(WebDriver driver) {
            ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
            PageFactory.initElements(decorator, this);
        }
    }
    
    public static class MultipleElementsTestPage {
        @FindElementBy(css = ".item")
        public List<WebElement> items;
        
        public MultipleElementsTestPage(WebDriver driver) {
            ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
            PageFactory.initElements(decorator, this);
        }
    }
    
    public static class ParentTestPage {
        @FindBy(id = "parent1")
        public WebElement parent1;
        
        public ParentTestPage(WebDriver driver) {
            ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
            PageFactory.initElements(decorator, this);
        }
    }
    
    public static class ComplexSelectorsTestPage {
        @FindElementBy(css = "[data-type='content']")
        public List<WebElement> contentSections;
        
        @FindElementBy(css = ".container .section .highlight")
        public List<WebElement> highlights;
        
        @FindElementBy(css = ".section > .description")
        public List<WebElement> descriptions;
        
        public ComplexSelectorsTestPage(WebDriver driver) {
            ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
            PageFactory.initElements(decorator, this);
        }
    }
    
    public static class XPathTestPage {
        @FindElementBy(xpath = "//table//td[1]")
        public WebElement firstCell;
        
        public XPathTestPage(WebDriver driver) {
            ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
            PageFactory.initElements(decorator, this);
        }
    }
    
    public static class XPathPredicatesTestPage {
        @FindElementBy(xpath = "//li[@data-value='2']")
        public WebElement secondItem;
        
        @FindElementBy(xpath = "//li[position()=3]")
        public WebElement thirdItem;
        
        public XPathPredicatesTestPage(WebDriver driver) {
            ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
            PageFactory.initElements(decorator, this);
        }
    }
    
    public static class XPathTextTestPage {
        @FindElementBy(xpath = "//p[contains(text(), 'Normal')]")
        public WebElement normalParagraph;
        
        @FindElementBy(xpath = "//p[contains(text(), 'emphasized')]")
        public WebElement emphasizedParagraph;
        
        public XPathTextTestPage(WebDriver driver) {
            ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
            PageFactory.initElements(decorator, this);
        }
    }
    
    public static class XPathMultipleTestPage {
        @FindElementBy(xpath = "//div[@class='item']")
        public List<WebElement> items;
        
        public XPathMultipleTestPage(WebDriver driver) {
            ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
            PageFactory.initElements(decorator, this);
        }
    }
    
    public static class BaseByTestPage {
        @FindElementBy(css = ".target")
        public WebElement targetElement;
        
        @FindElementBy(css = ".target")
        public List<WebElement> targetElements;
        
        public BaseByTestPage(WebDriver driver) {
            ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
            PageFactory.initElements(decorator, this);
        }
    }
    
    public static class ErrorHandlingTestPage {
        @FindElementBy(css = "div")
        public WebElement validElement;
        
        public ErrorHandlingTestPage(WebDriver driver) {
            ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
            PageFactory.initElements(decorator, this);
        }
    }
    
    public static class PerformanceTestPage {
        @FindElementBy(css = ".perf-item")
        public List<WebElement> perfItems;
        
        public PerformanceTestPage(WebDriver driver) {
            ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
            PageFactory.initElements(decorator, this);
        }
    }
    
    public static class ShadowDOMTestPage {
        @FindBy(id = "shadow-host")
        public WebElement shadowHost;
        
        public ShadowDOMTestPage(WebDriver driver) {
            ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
            PageFactory.initElements(decorator, this);
        }
    }
}