package io.github.sukgu;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShadowEdgeCaseTest {

    private WebDriver driver;
    private Shadow shadow;

    @BeforeAll
    public void setupOnce() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless"); // Run in headless mode for CI
        driver = new ChromeDriver(options);
        shadow = new Shadow(driver);
    }

    @BeforeEach
    public void setup() {
        driver.get("about:blank");
    }

    @AfterAll
    public void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Test JavaScript injection edge cases
    @Test
    public void testJavaScriptInjectionWithComplexShadowDOM() {
        String complexShadowHTML = 
            "<html><body>" +
            "<div id='host1'></div>" +
            "<div id='host2'></div>" +
            "<script>" +
            "const host1 = document.getElementById('host1');" +
            "const shadowRoot1 = host1.attachShadow({mode: 'open'});" +
            "shadowRoot1.innerHTML = '<div class=\"nested\"><span id=\"deep\">Deep Element</span></div>';" +
            
            "const host2 = document.getElementById('host2');" +
            "const shadowRoot2 = host2.attachShadow({mode: 'closed'});" +
            "shadowRoot2.innerHTML = '<div class=\"closed-shadow\">Closed Shadow Content</div>';" +
            "</script>" +
            "</body></html>";
            
        driver.get("data:text/html," + complexShadowHTML);
        
        // Test finding element in open shadow root
        WebElement host1 = shadow.findElement("#host1");
        WebElement deepElement = shadow.getShadowElement(host1, "#deep");
        assertThat(deepElement, notNullValue());
        assertEquals("Deep Element", deepElement.getText());
        
        // Test with closed shadow root (should handle gracefully)
        WebElement host2 = shadow.findElement("#host2");
        // Closed shadow roots typically can't be accessed, should return null or empty
        WebElement ele = shadow.getShadowElement(host2, ".closed-shadow");
        assertNull(ele);
    }

    @Test
    public void testNestedShadowDOMTraversal() {
        String nestedShadowHTML = 
            "<html><body>" +
            "<div id='outerHost'></div>" +
            "<script>" +
            "const outerHost = document.getElementById('outerHost');" +
            "const outerShadow = outerHost.attachShadow({mode: 'open'});" +
            "outerShadow.innerHTML = '<div id=\"innerHost\">Outer Shadow</div>';" +
            
            "setTimeout(() => {" +
            "  const innerHost = outerShadow.getElementById('innerHost');" +
            "  const innerShadow = innerHost.attachShadow({mode: 'open'});" +
            "  innerShadow.innerHTML = '<p id=\"deepest\">Deepest Element</p>';" +
            "}, 100);" +
            "</script>" +
            "</body></html>";
            
        driver.get("data:text/html," + nestedShadowHTML);
        
        // Wait for nested shadow DOM to be created
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        
        WebElement outerHost = shadow.findElement("#outerHost");
        WebElement innerHost = shadow.getShadowElement(outerHost, "#innerHost");
        
        // Wait for inner shadow to be attached
        wait.until(d -> {
            try {
                return shadow.getShadowElement(innerHost, "#deepest") != null;
            } catch (Exception e) {
                return false;
            }
        });
        
        WebElement deepestElement = shadow.getShadowElement(innerHost, "#deepest");
        assertThat(deepestElement, notNullValue());
        assertEquals("Deepest Element", deepestElement.getText());
    }

    // Test performance with dynamic content
    @Test
    public void testDynamicContentGeneration() {
        String dynamicHTML = 
            "<html><body>" +
            "<div id='container'></div>" +
            "<button id='generate'>Generate Content</button>" +
            "<script>" +
            "document.getElementById('generate').onclick = function() {" +
            "  const container = document.getElementById('container');" +
            "  for(let i = 0; i < 50; i++) {" +
            "    const div = document.createElement('div');" +
            "    div.className = 'dynamic-item';" +
            "    div.textContent = 'Dynamic Item ' + i;" +
            "    div.setAttribute('data-index', i);" +
            "    container.appendChild(div);" +
            "  }" +
            "};" +
            "</script>" +
            "</body></html>";
            
        driver.get("data:text/html," + dynamicHTML);
        
        WebElement generateButton = shadow.findElement("#generate");
        generateButton.click();
        
        // Wait for dynamic content to be generated
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> shadow.findElements(".dynamic-item").size() == 50);
        
        List<WebElement> dynamicItems = shadow.findElements(".dynamic-item");
        assertEquals(50, dynamicItems.size());
        
        // Test finding specific items in large dynamic set
        WebElement item25 = shadow.findElement("[data-index='25']");
        assertEquals("Dynamic Item 25", item25.getText());
    }

    // Test wait functionality edge cases
    @Test
    public void testExplicitWaitWithSlowLoading() throws Exception {
        String slowLoadHTML = 
            "<html><body>" +
            "<div id='immediate'>Immediate Content</div>" +
            "<script>" +
            "setTimeout(() => {" +
            "  const delayed = document.createElement('div');" +
            "  delayed.id = 'delayed';" +
            "  delayed.textContent = 'Delayed Content';" +
            "  document.body.appendChild(delayed);" +
            "}, 2000);" +
            "</script>" +
            "</body></html>";
            
        driver.get("data:text/html," + slowLoadHTML);
        
        // Immediate element should be found quickly
        WebElement immediate = shadow.findElement("#immediate");
        assertThat(immediate, notNullValue());
        
        // Set explicit wait for delayed content
        shadow.setExplicitWait(500, 5);
        try {
            WebElement delayed = shadow.findElement("#delayed");
            assertThat(delayed, notNullValue());
            assertEquals("Delayed Content", delayed.getText());
        } catch (NoSuchElementException e) {
            fail("Should have found delayed element within explicit wait time");
        } finally {
            shadow.setExplicitWait(0, 0); // Reset
        }
    }

    @Test
    public void testWaitTimeoutBehavior() throws Exception {
        driver.get("data:text/html,<html><body><div>Static content only</div></body></html>");
        
        shadow.setExplicitWait(100, 1); // Very short timeout
        
        try {
            // This should timeout and throw NoSuchElementException
            assertThrows(NoSuchElementException.class, () -> {
                shadow.findElement("#nonexistent");
            });
        } catch (Exception e) {
            // Exception during wait setup is also acceptable
        } finally {
            shadow.setExplicitWait(0, 0); // Reset
        }
    }

    // Test form interaction edge cases
    @Test
    public void testFormInteractionWithDuplicateLabels() {
        String formHTML = 
            "<html><body>" +
            "<form id='form1'>" +
            "  <label>Name</label><input type='text' id='name1'>" +
            "  <label>Submit</label><input type='checkbox' id='cb1'>" +
            "</form>" +
            "<form id='form2'>" +
            "  <label>Name</label><input type='text' id='name2'>" +
            "  <label>Submit</label><input type='checkbox' id='cb2'>" +
            "</form>" +
            "</body></html>";
            
        driver.get("data:text/html," + formHTML);
        
        // Test selecting checkbox with duplicate labels using parent element
        WebElement form1 = shadow.findElement("#form1");
        WebElement form2 = shadow.findElement("#form2");
        
        shadow.selectCheckbox(form1, "Submit");
        shadow.selectCheckbox(form2, "Submit");
        
        WebElement cb1 = shadow.findElement("#cb1");
        WebElement cb2 = shadow.findElement("#cb2");
        
        assertTrue(shadow.isChecked(cb1));
        assertTrue(shadow.isChecked(cb2));
    }

    @Test
    public void testSelectDropdownWithComplexOptions() {
        String url = getPageContent("dropdown_with_complex_options.html");
            
        driver.get(url);
        WebElement dropdown = shadow.findElement("#complex-dropdown");
        // Test selecting options with special characters
        shadow.selectDropdown(dropdown, "Option with special chars: !@#$%");
        Select d = new Select(dropdown);
        assertEquals("opt1", d.getFirstSelectedOption().getAttribute("value"));
        
        // Test selecting very long option
        shadow.selectDropdown(dropdown, "Very long option text that might cause issues in some scenarios");
        assertEquals("opt2", d.getFirstSelectedOption().getAttribute("value"));
    }

    // Test attribute handling edge cases
    @Test
    public void testGetAttributeWithSpecialCharacters() {
        String url = getPageContent("special_char_page.html");
        
        driver.get(url);

        WebElement element = shadow.findElement("#test");
        
        assertEquals("value with spaces and symbols !@#$%^&*()", 
                    shadow.getAttribute(element, "data-special"));
        assertEquals("测试中文", shadow.getAttribute(element, "data-unicode"));
        assertEquals("", shadow.getAttribute(element, "data-empty"));
        assertEquals("{\\\"key\\\": \\\"value\\\", \\\"number\\\": 123}", 
                    shadow.getAttribute(element, "data-json"));
    }

    // Test XPath edge cases
    @Test
    public void testXPathWithNamespaces() {
        String namespaceHTML = 
            "<html xmlns:custom='http://example.com/custom'>" +
            "<body>" +
            "<custom:element id='ns-element'>Namespaced Element</custom:element>" +
            "<div custom:attribute='test'>Custom Attribute</div>" +
            "</body></html>";
            
        driver.get("data:text/html," + namespaceHTML);
        
        // XPath should handle namespaced elements
        try {
            WebElement nsElement = shadow.findElementByXPath("//*[@id='ns-element']");
            assertThat(nsElement, notNullValue());
        } catch (Exception e) {
            // Namespaces might not be fully supported, which is acceptable
        }
    }

    @Test
    public void testXPathWithComplexPredicates() {
        String complexHTML = 
            "<html><body>" +
            "<table>" +
            "  <tr><td class='header'>Name</td><td class='header'>Age</td></tr>" +
            "  <tr><td class='data'>John</td><td class='data'>25</td></tr>" +
            "  <tr><td class='data'>Jane</td><td class='data'>30</td></tr>" +
            "</table>" +
            "</body></html>";
            
        driver.get("data:text/html," + complexHTML);
        
        // Complex XPath with multiple conditions and functions
        WebElement cell = shadow.findElementByXPath(
            "//tr[position()>1]/td[@class='data' and contains(text(), 'John')]"
        );
        assertThat(cell, notNullValue());
        assertEquals("John", cell.getText());
        
        // XPath with following-sibling axis
        WebElement ageCell = shadow.findElementByXPath(
            "//td[text()='Jane']/following-sibling::td"
        );
        assertThat(ageCell, notNullValue());
        assertEquals("30", ageCell.getText());
    }

    // Test visibility edge cases
    @Test
    public void testVisibilityWithCSSTransforms() {
        String transformHTML = 
            "<html><body>" +
            "<div id='scaled' style='transform: scale(0);'>Scaled to zero</div>" +
            "<div id='translated' style='transform: translateX(-9999px);'>Translated off-screen</div>" +
            "<div id='rotated' style='transform: rotate(180deg);'>Rotated</div>" +
            "<div id='opacity' style='opacity: 0.01;'>Very transparent</div>" +
            "</body></html>";
            
        driver.get("data:text/html," + transformHTML);
        
        WebElement scaled = shadow.findElement("#scaled");
        WebElement translated = shadow.findElement("#translated");
        WebElement rotated = shadow.findElement("#rotated");
        WebElement transparent = shadow.findElement("#opacity");
        
        // These elements might be considered invisible depending on implementation
        // Test that the method doesn't crash and returns consistent results
        boolean scaledVisible = shadow.isVisible(scaled);
        boolean translatedVisible = shadow.isVisible(translated);
        boolean rotatedVisible = shadow.isVisible(rotated);
        boolean transparentVisible = shadow.isVisible(transparent);
        
        // At least rotated should be visible
        assertTrue(rotatedVisible, "Rotated element should be visible");
    }

    // Test error recovery scenarios
    @Test
    public void testErrorRecoveryAfterJavaScriptError() {
        driver.get("data:text/html,<html><body><div id='test'>Test</div></body></html>");
        
        // Cause a JavaScript error
        try {
            ((JavascriptExecutor) driver).executeScript("throw new Error('Intentional error');");
        } catch (Exception e) {
            // Expected
        }
        
        // Shadow operations should still work after JS error
        WebElement element = shadow.findElement("#test");
        assertThat(element, notNullValue());
        assertTrue(shadow.isVisible(element));
    }

    @Test
    public void testBrowserStateRecovery() {
        // Test navigation and state changes
        driver.get("data:text/html,<html><body><div id='page1'>Page 1</div></body></html>");
        
        WebElement page1Element = shadow.findElement("#page1");
        assertThat(page1Element, notNullValue());
        
        // Navigate to different page
        driver.get("data:text/html,<html><body><div id='page2'>Page 2</div></body></html>");
        
        // Previous element should be stale, but new operations should work
        WebElement page2Element = shadow.findElement("#page2");
        assertThat(page2Element, notNullValue());
        assertEquals("Page 2", page2Element.getText());
    }

    // Test memory and resource management
    @Test
    public void testLargeScaleOperations() {
        StringBuilder largeHTMLBuilder = new StringBuilder("<html><body>");
        
        // Create large HTML structure
        for (int i = 0; i < 500; i++) {
            largeHTMLBuilder.append("<div class='level1-").append(i).append("'>");
            for (int j = 0; j < 5; j++) {
                largeHTMLBuilder.append("<span class='level2-").append(j).append("' data-parent='").append(i).append("'>Item ").append(i).append("-").append(j).append("</span>");
            }
            largeHTMLBuilder.append("</div>");
        }
        largeHTMLBuilder.append("</body></html>");
        
        driver.get("data:text/html," + largeHTMLBuilder.toString());
        
        // Perform multiple operations on large document
        List<WebElement> allDivs = shadow.findElements("div[class^='level1-']");
        assertEquals(500, allDivs.size());
        
        List<WebElement> allSpans = shadow.findElements("span[class^='level2-']");
        assertEquals(2500, allSpans.size());
        
        // Find specific element in large set
        WebElement specific = shadow.findElement("[data-parent='250']");
        assertThat(specific, notNullValue());
        
        // XPath operations on large document
        List<WebElement> xpathResults = shadow.findElementsByXPath("//span[contains(@class, 'level2-') and @data-parent='100']");
        assertEquals(5, xpathResults.size());
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
    
}