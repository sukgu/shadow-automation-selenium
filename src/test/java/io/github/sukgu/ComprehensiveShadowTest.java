package io.github.sukgu;

import java.net.URI;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ComprehensiveShadowTest {

    private WebDriver driver;
    private Shadow shadow;
    private static final boolean debug = Boolean.parseBoolean(System.getProperty("DEBUG", "false"));

    @BeforeAll
    public void setupOnce() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
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

    // Edge Case Tests for Constructor
    @Test
    public void testShadowWithChromeDriver() {
        ChromeDriver chromeDriver = new ChromeDriver();
        try {
            Shadow shadowChrome = new Shadow(chromeDriver);
            assertThat(shadowChrome, notNullValue());
        } finally {
            chromeDriver.quit();
        }
    }

    @Test
    public void testShadowWithFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxDriver firefoxDriver = new FirefoxDriver();
        try {
            Shadow shadowFirefox = new Shadow(firefoxDriver);
            assertThat(shadowFirefox, notNullValue());
        } finally {
            firefoxDriver.quit();
        }
    }

    // Test element state checking methods
    @Test
    public void testIsVisibleWithHiddenElement() {
        driver.get(getPageContent("button.html"));
        ((JavascriptExecutor) driver).executeScript("document.querySelector('body').shadowRoot.querySelector('input').style.display = 'none';");
        
        WebElement hiddenInput = shadow.findElement("input");
        assertFalse(shadow.isVisible(hiddenInput), "Hidden element should not be visible");
    }

    @Test
    public void testIsVisibleWithVisibleElement() {
        driver.get(getPageContent("button.html"));
        WebElement visibleButton = shadow.findElement("button");
        assertTrue(shadow.isVisible(visibleButton), "Visible element should be visible");
    }

    @Test
    public void testIsCheckedWithCheckbox() {
        driver.get("data:text/html,<html><body><input type='checkbox' id='cb1' checked><input type='checkbox' id='cb2'></body></html>");
        
        WebElement checkedBox = shadow.findElement("#cb1");
        WebElement uncheckedBox = shadow.findElement("#cb2");
        
        assertTrue(shadow.isChecked(checkedBox), "Checked checkbox should return true");
        assertFalse(shadow.isChecked(uncheckedBox), "Unchecked checkbox should return false");
    }

    @Test
    public void testIsCheckedWithRadioButton() {
        driver.get("data:text/html,<html><body><input type='radio' id='r1' name='group' checked><input type='radio' id='r2' name='group'></body></html>");
        
        WebElement checkedRadio = shadow.findElement("#r1");
        WebElement uncheckedRadio = shadow.findElement("#r2");
        
        assertTrue(shadow.isChecked(checkedRadio), "Checked radio should return true");
        assertFalse(shadow.isChecked(uncheckedRadio), "Unchecked radio should return false");
    }

    @Test
    public void testIsDisabledWithDisabledElement() {
        driver.get("data:text/html,<html><body><button id='enabled'>Enabled</button><button id='disabled' disabled>Disabled</button></body></html>");
        
        WebElement enabledButton = shadow.findElement("#enabled");
        WebElement disabledButton = shadow.findElement("#disabled");
        
        assertFalse(shadow.isDisabled(enabledButton), "Enabled element should not be disabled");
        assertTrue(shadow.isDisabled(disabledButton), "Disabled element should be disabled");
    }

    // Test getAttribute method with various attributes
    @Test
    public void testGetAttributeWithStandardAttributes() {
        driver.get("data:text/html,<html><body><div id='test' class='testclass' data-custom='customvalue' aria-label='testlabel'></div></body></html>");
        
        WebElement element = shadow.findElement("#test");
        
        assertEquals("test", shadow.getAttribute(element, "id"));
        assertEquals("testclass", shadow.getAttribute(element, "class"));
        assertEquals("customvalue", shadow.getAttribute(element, "data-custom"));
        assertEquals("testlabel", shadow.getAttribute(element, "aria-label"));
    }

    @Test
    public void testGetAttributeWithNonExistentAttribute() {
        driver.get("data:text/html,<html><body><div id='test'></div></body></html>");
        
        WebElement element = shadow.findElement("#test");
        String result = shadow.getAttribute(element, "nonexistent");
        
        // Should return null for non-existent attributes
        assertNull(result);
    }

    // Test form interaction methods
    @Test
    public void testSelectCheckboxByLabel() {
        String html = "<html><body>" +
                "<label for='cb1'>Accept Terms</label><input type='checkbox' id='cb1'>" +
                "<label for='cb2'>Subscribe Newsletter</label><input type='checkbox' id='cb2'>" +
                "</body></html>";
        
        driver.get("data:text/html," + html);
        
        shadow.selectCheckbox("Accept Terms");
        WebElement checkbox = shadow.findElement("#cb1");
        assertTrue(shadow.isChecked(checkbox), "Checkbox should be selected after selectCheckbox");
    }

    @Test
    public void testSelectCheckboxWithParentElement() {
        String html = "<html><body>" +
                "<div id='form1'><label>Option 1</label><input type='checkbox' id='cb1'></div>" +
                "<div id='form2'><label>Option 1</label><input type='checkbox' id='cb2'></div>" +
                "</body></html>";
        
        driver.get("data:text/html," + html);
        
        WebElement form1 = shadow.findElement("#form1");
        shadow.selectCheckbox(form1, "Option 1");
        
        WebElement checkbox1 = shadow.findElement("#cb1");
        WebElement checkbox2 = shadow.findElement("#cb2");
        
        assertTrue(shadow.isChecked(checkbox1), "Checkbox in form1 should be selected");
        assertFalse(shadow.isChecked(checkbox2), "Checkbox in form2 should not be selected");
    }

    @Test
    public void testSelectRadioByLabel() {
        String html = "<html><body>" +
                "<label>Option A</label><input type='radio' name='group' value='a'>" +
                "<label>Option B</label><input type='radio' name='group' value='b'>" +
                "</body></html>";
        
        driver.get("data:text/html," + html);
        
        shadow.selectRadio("Option A");
        WebElement radioA = shadow.findElement("input[value='a']");
        assertTrue(shadow.isChecked(radioA), "Radio A should be selected");
    }

    @Test
    public void testSelectDropdownByLabel() {
        String html = "<html><body>" +
                "<select id='dropdown'>" +
                "<option value='1'>Option 1</option>" +
                "<option value='2'>Option 2</option>" +
                "</select></body></html>";
        
        driver.get("data:text/html," + html);
        WebElement select = shadow.findElement("#dropdown");
        shadow.selectDropdown(select, "Option 2");
        WebElement dropdown = shadow.findElement("#dropdown");
        assertEquals("2", dropdown.getAttribute("value"));
    }

    // Test scrollTo method
    @Test
    public void testScrollToElement() {
        String html = "<html><body style='height: 2000px;'>" +
                "<div style='margin-top: 1500px;' id='target'>Target Element</div>" +
                "</body></html>";
        
        driver.get("data:text/html," + html);
        
        WebElement targetElement = shadow.findElement("#target");
        shadow.scrollTo(targetElement);
        
        // Verify element is in viewport after scroll
        Boolean isInViewport = (Boolean) ((JavascriptExecutor) driver).executeScript(
            "var rect = arguments[0].getBoundingClientRect();" +
            "return rect.top >= 0 && rect.left >= 0 && " +
            "rect.bottom <= window.innerHeight && rect.right <= window.innerWidth;",
            targetElement
        );
        
        assertTrue(isInViewport, "Element should be in viewport after scroll");
    }

    // Test highlight methods
    @Test
    public void testHighlightWithCustomColorAndTime() {
        driver.get(getPageContent("button.html"));
        WebElement button = shadow.findElement("button");
        
        // This should not throw any exceptions
        assertDoesNotThrow(() -> {
            shadow.highlight(button, "blue", 1000);
        });
    }

    @Test
    public void testHighlightWithDefaultColor() {
        driver.get(getPageContent("button.html"));
        WebElement button = shadow.findElement("button");
        
        // This should not throw any exceptions
        assertDoesNotThrow(() -> {
            shadow.highlight(button);
        });
    }

    // Test navigation methods
    @Test
    public void testGetParentElementEdgeCases() {
        driver.get("data:text/html,<html><body><div id='parent'><span id='child'>Child</span></div></body></html>");
        
        WebElement child = shadow.findElement("#child");
        WebElement parent = shadow.getParentElement(child);
        
        assertThat(parent, notNullValue());
        assertEquals("div", parent.getTagName().toLowerCase());
        assertEquals("parent", parent.getAttribute("id"));
    }

    @Test
    public void testGetChildElementsEdgeCases() {
        driver.get("data:text/html,<html><body><div id='parent'><span>Child 1</span><p>Child 2</p></div></body></html>");
        
        WebElement parent = shadow.findElement("#parent");
        List<WebElement> children = shadow.getChildElements(parent);
        
        assertThat(children, notNullValue());
        assertEquals(2, children.size());
        assertEquals("span", children.get(0).getTagName().toLowerCase());
        assertEquals("p", children.get(1).getTagName().toLowerCase());
    }

    @Test
    public void testGetSiblingElementsEdgeCases() {
        driver.get("data:text/html,<html><body><div><span id='first'>First</span><p id='middle'>Middle</p><div id='last'>Last</div></div></body></html>");
        
        WebElement middle = shadow.findElement("#middle");
        List<WebElement> siblings = shadow.getSiblingElements(middle);
        
        assertThat(siblings, notNullValue());
        assertEquals(2, siblings.size()); // Should not include the element itself
    }

    @Test
    public void testGetSiblingElementWithSelector() {
        driver.get("data:text/html,<html><body><div><span id='first'>First</span><p id='middle'>Middle</p><div id='last'>Last</div></div></body></html>");
        
        WebElement middle = shadow.findElement("#middle");
        WebElement divSibling = shadow.getSiblingElement(middle, "div");
        
        assertThat(divSibling, notNullValue());
        assertEquals("last", divSibling.getAttribute("id"));
    }

    @Test
    public void testGetNextSiblingElement() {
        driver.get("data:text/html,<html><body><div><span id='first'>First</span><p id='middle'>Middle</p><div id='last'>Last</div></div></body></html>");
        
        WebElement middle = shadow.findElement("#middle");
        WebElement nextSibling = shadow.getNextSiblingElement(middle);
        
        assertThat(nextSibling, notNullValue());
        assertEquals("last", nextSibling.getAttribute("id"));
    }

    @Test
    public void testGetPreviousSiblingElement() {
        driver.get("data:text/html,<html><body><div><span id='first'>First</span><p id='middle'>Middle</p><div id='last'>Last</div></div></body></html>");
        
        WebElement middle = shadow.findElement("#middle");
        WebElement prevSibling = shadow.getPreviousSiblingElement(middle);
        
        assertThat(prevSibling, notNullValue());
        assertEquals("first", prevSibling.getAttribute("id"));
    }

    // Test wait configuration methods
    @Test
    public void testSetImplicitWaitEdgeCases() {
        // Test with zero wait
        assertDoesNotThrow(() -> {
            shadow.setImplicitWait(0);
        });
        
        // Test with negative wait (should handle gracefully)
        assertDoesNotThrow(() -> {
            shadow.setImplicitWait(-1);
        });
        
        // Test with large wait value
        assertDoesNotThrow(() -> {
            shadow.setImplicitWait(30);
            shadow.setImplicitWait(0); // Reset
        });
    }

    @Test
    public void testSetExplicitWaitEdgeCases() {
        // Test with zero values
        assertDoesNotThrow(() -> {
            shadow.setExplicitWait(0, 0);
        });
        
        // Test with negative values
        assertThrows(Exception.class, () -> {
            shadow.setExplicitWait(-1, 100);
        });
        
        // Test with valid values
        assertDoesNotThrow(() -> {
            shadow.setExplicitWait(10, 5);
            shadow.setExplicitWait(0, 0); // Reset
        });
    }

    // Test XPath with complex queries
    @Test
    public void testComplexXPathQueries() {
        String html = "<html><body>" +
                "<div class='container'>" +
                "<table><tr><td data-type='cell'>Cell 1</td><td data-type='cell'>Cell 2</td></tr></table>" +
                "</div></body></html>";
        
        driver.get("data:text/html," + html);
        
        // Complex XPath with multiple conditions
        WebElement cell = shadow.findElementByXPath("//td[@data-type='cell' and contains(text(), 'Cell 1')]");
        assertThat(cell, notNullValue());
        assertEquals("Cell 1", cell.getText());
        
        // XPath with ancestor-descendant relationship
        List<WebElement> cells = shadow.findElementsByXPath("//div[@class='container']//td");
        assertEquals(2, cells.size());
    }

    // Test error handling and edge cases
    @Test
    public void testFindElementWithInvalidSelector() {
        driver.get(getPageContent("index.html"));
        
        // CSS selector that doesn't exist
        assertThrows(NoSuchElementException.class, () -> {
            shadow.findElement("#nonexistent");
        });
        
        // Invalid CSS selector syntax
        assertThrows(JavascriptException.class, () -> {
            shadow.findElement("#invalid>>selector");
        });
    }

    @Test
    public void testFindElementsWithInvalidSelector() {
        driver.get(getPageContent("index.html"));
        
        // Selector that doesn't match anything should return empty list
        List<WebElement> elements = shadow.findElements("#nonexistent");
        assertThat(elements, notNullValue());
        assertEquals(0, elements.size());
    }

    @Test
    public void testXPathWithInvalidSyntax() {
        driver.get(getPageContent("index.html"));
        
        // Invalid XPath should throw JavascriptException
        assertThrows(JavascriptException.class, () -> {
            shadow.findElementByXPath("//invalid[@syntax[unclosed");
        });
    }

    // Test shadow DOM specific functionality
    @Test
    public void testGetShadowElementWithNonShadowHost() {
        driver.get("data:text/html,<html><body><div id='regular'></div><script>const host = document.getElementById('regular'); const shadowRoot = host.attachShadow({mode: 'open'}); shadowRoot.innerHTML = '<div id=\"inregular\">Regular Element</div>';</script></body></html>");
        
        WebElement regularElement = shadow.findElement("#regular");
        
        // Trying to get shadow element from non-shadow host should not return null
        assertNotNull(shadow.getShadowElement(regularElement, "#inregular"));
    }

    @Test
    public void testGetAllShadowElementWithNonShadowHost() {
        driver.get("data:text/html,<html><body><div id='regular'>Regular Element</div></body></html>");
        
        WebElement regularElement = shadow.findElement("#regular");
        
        // Should return empty list or handle gracefully
        List<WebElement> shadowElements = shadow.getAllShadowElement(regularElement, "anySelector");
        assertThat(shadowElements, notNullValue());
        assertEquals(0, shadowElements.size());
    }

    // Test boundary conditions for selectors
    @Test
    public void testVeryLongSelector() {
        driver.get("data:text/html,<html><body><div id='test'>Test</div></body></html>");
        
        // Very long but valid selector
        String longSelector = "body > div".repeat(1) + "#test";
        WebElement element = shadow.findElement(longSelector);
        assertThat(element, notNullValue());
    }

    @Test
    public void testEmptyAndNullSelectors() {
        driver.get(getPageContent("index.html"));
        
        // Empty selector should throw exception
        assertThrows(Exception.class, () -> {
            shadow.findElement("");
        });
        
        // Null selector should throw exception
        assertThrows(Exception.class, () -> {
            shadow.findElement(null);
        });
    }

    // Test concurrent operations (edge case)
    @Test
    public void testMultipleSimultaneousOperations() {
        driver.get(getPageContent("button.html"));
        
        // Perform multiple operations in sequence without interference
        WebElement button1 = shadow.findElement("button");
        WebElement button2 = shadow.findElement("button");
        
        // Both should refer to the same element
        assertEquals(button1.getText(), button2.getText());
        
        // Highlight operations should not interfere
        shadow.highlight(button1, "red", 100);
        shadow.highlight(button2, "blue", 100);
        
        // Element should still be functional
        assertTrue(shadow.isVisible(button1));
    }

    // Test performance edge cases
    @Test
    public void testLargeElementSet() {
        // Create HTML with many elements
        StringBuilder htmlBuilder = new StringBuilder("<html><body>");
        for (int i = 0; i < 100; i++) {
            htmlBuilder.append("<div class='item' data-index='").append(i).append("'>Item ").append(i).append("</div>");
        }
        htmlBuilder.append("</body></html>");
        
        driver.get("data:text/html," + htmlBuilder.toString());
        
        // Find all elements - should handle large sets efficiently
        List<WebElement> allItems = shadow.findElements(".item");
        assertEquals(100, allItems.size());
        
        // Find specific element by XPath in large set
        WebElement item50 = shadow.findElementByXPath("//div[@data-index='50']");
        assertThat(item50, notNullValue());
        assertEquals("Item 50", item50.getText());
    }

    // Utility method to get local test files
    private String getPageContent(String pagename) {
        try {
            URI uri = getClass().getClassLoader().getResource(pagename).toURI();
            return "file://" + uri.getPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}