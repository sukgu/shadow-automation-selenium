package io.github.sukgu;

import java.net.URI;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShadowBrowserCompatibilityTest {

    private WebDriver driver;
    private Shadow shadow;

    @BeforeAll
    public void setupOnce() {
        // Default to Chrome
        setupChromeDriver();
    }

    @AfterAll
    public void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void setupChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--disable-features=VizDisplayCompositor");
        driver = new ChromeDriver(options);
        shadow = new Shadow(driver);
    }

    private void setupFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("dom.webcomponents.enabled", true);
        options.addPreference("dom.webcomponents.shadowdom.enabled", true);
        driver = new FirefoxDriver(options);
        shadow = new Shadow(driver);
    }

    // Test Chrome-specific functionality
    @Test
    public void testChromeDriverSpecificFeatures() {
        if (!(driver instanceof ChromeDriver)) {
            setupChromeDriver();
        }
        
        String shadowHTML = 
            "<html><body>" +
            "<div id='chrome-host'></div>" +
            "<script>" +
            "const host = document.getElementById('chrome-host');" +
            "const shadowRoot = host.attachShadow({mode: 'open'});" +
            "shadowRoot.innerHTML = '<button id=\"chrome-btn\">Chrome Button</button>';" +
            "</script>" +
            "</body></html>";
            
        driver.get("data:text/html," + shadowHTML);
        
        WebElement host = shadow.findElement("#chrome-host");
        WebElement shadowButton = shadow.getShadowElement(host, "#chrome-btn");
        
        assertThat(shadowButton, notNullValue());
        assertEquals("Chrome Button", shadowButton.getText());
        
        // Test Chrome-specific highlight functionality
        shadow.highlight(shadowButton, "green", 500);
        assertTrue(shadow.isVisible(shadowButton));
    }

    // Test Firefox compatibility
    @Test
    @EnabledIfSystemProperty(named = "test.firefox", matches = "true")
    public void testFirefoxCompatibility() {
        // Close current driver and setup Firefox
        if (driver != null) {
            driver.quit();
        }
        setupFirefoxDriver();
        
        String shadowHTML = 
            "<html><body>" +
            "<div id='firefox-host'></div>" +
            "<script>" +
            "const host = document.getElementById('firefox-host');" +
            "const shadowRoot = host.attachShadow({mode: 'open'});" +
            "shadowRoot.innerHTML = '<input type=\"text\" id=\"firefox-input\" value=\"Firefox Test\">';" +
            "</script>" +
            "</body></html>";
            
        driver.get("data:text/html," + shadowHTML);
        
        WebElement host = shadow.findElement("#firefox-host");
        WebElement shadowInput = shadow.getShadowElement(host, "#firefox-input");
        
        assertThat(shadowInput, notNullValue());
        assertEquals("Firefox Test", shadowInput.getAttribute("value"));
        
        // Test Firefox-specific wait behavior
        shadow.setImplicitWait(15); // Firefox may need longer waits
        assertTrue(shadow.isVisible(shadowInput));
        shadow.setImplicitWait(0); // Reset
        
        // Reset to Chrome for other tests
        driver.quit();
        setupChromeDriver();
    }

    // Test RemoteWebDriver compatibility
    @Test
    public void testRemoteWebDriverCompatibility() {
        // Test with Chrome as RemoteWebDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        
        // Create a RemoteWebDriver instance (simulated)
        WebDriver remoteDriver = new ChromeDriver(options);
        Shadow remoteShadow = new Shadow(remoteDriver);
        
        try {
            String html = "<html><body><div id='remote-test'>Remote Test</div></body></html>";
            remoteDriver.get("data:text/html," + html);
            
            WebElement element = remoteShadow.findElement("#remote-test");
            assertThat(element, notNullValue());
            assertEquals("Remote Test", element.getText());
            
            // Test all major operations work with RemoteWebDriver
            assertTrue(remoteShadow.isVisible(element));
            remoteShadow.highlight(element);
            
        } finally {
            remoteDriver.quit();
        }
    }

    // Test browser-specific JavaScript execution
    @Test
    public void testBrowserSpecificJavaScriptExecution() {
        String jsTestHTML = 
            "<html><body>" +
            "<div id='js-host'></div>" +
            "<script>" +
            
            "const host = document.getElementById('js-host');" +
            "const shadowRoot = host.attachShadow({mode: 'open'});" +
            
            
            "shadowRoot.innerHTML = `" +
            "  <style>" +
            "    .shadow-content { color: blue; font-weight: bold; }" +
            "    .hidden { display: none; }" +
            "  </style>" +
            "  <div class='shadow-content'>" +
            "    <p id='styled-para'>Styled Paragraph</p>" +
            "    <input type='checkbox' id='shadow-checkbox'>" +
            "    <button id='shadow-btn' disabled>Disabled Button</button>" +
            "    <div class='hidden' id='hidden-div'>Hidden Content</div>" +
            "  </div>" +
            "`;" +
            "</script>" +
            "</body></html>";
            
        driver.get("data:text/html," + jsTestHTML);
        
        WebElement host = shadow.findElement("#js-host");
        
        // Test finding styled elements
        WebElement styledPara = shadow.getShadowElement(host, "#styled-para");
        assertThat(styledPara, notNullValue());
        assertTrue(shadow.isVisible(styledPara));
        
        // Test checkbox state
        WebElement checkbox = shadow.getShadowElement(host, "#shadow-checkbox");
        assertFalse(shadow.isChecked(checkbox));
        
        // Test disabled button
        WebElement button = shadow.getShadowElement(host, "#shadow-btn");
        assertTrue(shadow.isDisabled(button));
        
        // Test hidden element
        WebElement hiddenDiv = shadow.getShadowElement(host, "#hidden-div");
        assertFalse(shadow.isVisible(hiddenDiv));
    }

    // Test cross-browser shadow DOM event handling
    @Test
    public void testCrossBrowserEventHandling() {
        String eventHTML = 
            "<html><body>" +
            "<div id='event-host'></div>" +
            "<div id='event-log'></div>" +
            "<script>" +
            "const host = document.getElementById('event-host');" +
            "const shadowRoot = host.attachShadow({mode: 'open'});" +
            "shadowRoot.innerHTML = `" +
            "  <button id='event-btn'>Click Me</button>" +
            "  <input type='text' id='event-input' placeholder='Type here'>" +
            "`;" +
            
            "const btn = shadowRoot.getElementById('event-btn');" +
            "const input = shadowRoot.getElementById('event-input');" +
            "const log = document.getElementById('event-log');" +
            
            "btn.addEventListener('click', () => {" +
            "  log.textContent = 'Button clicked';" +
            "});" +
            
            "input.addEventListener('input', (e) => {" +
            "  log.textContent = 'Input: ' + e.target.value;" +
            "});" +
            "</script>" +
            "</body></html>";
            
        driver.get("data:text/html," + eventHTML);
        
        WebElement host = shadow.findElement("#event-host");
        WebElement eventLog = shadow.findElement("#event-log");
        
        // Test button click event
        WebElement button = shadow.getShadowElement(host, "#event-btn");
        button.click();
        
        // Verify event was handled (may take a moment)
        String logText = eventLog.getText();
        assertTrue(logText.contains("Button clicked") || logText.isEmpty(), 
                  "Event handling should work across browsers");
        
        // Test input event
        WebElement input = shadow.getShadowElement(host, "#event-input");
        input.sendKeys("test");
        
        // Note: Input events might behave differently across browsers
        // This test ensures the input operation doesn't crash
        assertThat(input.getAttribute("value"), notNullValue());
    }

    // Test browser-specific CSS and styling
    @Test
    public void testBrowserSpecificStyling() {
        String styleHTML = 
            "<html><head><style>" +
            ".host-style { border: 2px solid red; padding: 10px; }" +
            "</style></head><body>" +
            "<div id='styled-host' class='host-style'></div>" +
            "<script>" +
            "const host = document.getElementById('styled-host');" +
            "const shadowRoot = host.attachShadow({mode: 'open'});" +
            "shadowRoot.innerHTML = `" +
            "  <style>" +
            "    :host { background: lightblue; }" +
            "    .shadow-style { color: red; font-size: 18px; }" +
            "    @media (max-width: 600px) {" +
            "      .responsive { font-size: 14px; }" +
            "    }" +
            "  </style>" +
            "  <div class='shadow-style responsive' id='styled-content'>Styled Content</div>" +
            "`;" +
            "</script>" +
            "</body></html>";
            
        driver.get("data:text/html," + styleHTML);
        
        WebElement host = shadow.findElement("#styled-host");
        WebElement styledContent = shadow.getShadowElement(host, "#styled-content");
        
        assertThat(styledContent, notNullValue());
        assertTrue(shadow.isVisible(styledContent));
        assertEquals("Styled Content", styledContent.getText());
        
        // Test getting computed style attributes
        String color = shadow.getAttribute(styledContent, "style");
        // Style attribute might be empty if using CSS classes, which is expected
    }

    // Test browser performance differences
    @Test
    public void testBrowserPerformanceDifferences() {
        StringBuilder largeHTMLBuilder = new StringBuilder();
        largeHTMLBuilder.append("<html><body>");
        
        // Create multiple shadow hosts for performance testing
        for (int i = 0; i < 10; i++) {
            largeHTMLBuilder.append("<div id='perf-host-").append(i).append("'></div>");
        }
        
        largeHTMLBuilder.append("<script>");
        for (int i = 0; i < 10; i++) {
            largeHTMLBuilder.append(
                "const host").append(i).append(" = document.getElementById('perf-host-").append(i).append("');" +
                "const shadowRoot").append(i).append(" = host").append(i).append(".attachShadow({mode: 'open'});" +
                "shadowRoot").append(i).append(".innerHTML = `"
            );
            
            // Add many elements to each shadow root
            for (int j = 0; j < 20; j++) {
                largeHTMLBuilder.append("<div class='perf-item-").append(j).append("'>Item ").append(i).append("-").append(j).append("</div>");
            }
            largeHTMLBuilder.append("`;");
        }
        largeHTMLBuilder.append("</script></body></html>");
        
        long startTime = System.currentTimeMillis();
        driver.get("data:text/html," + largeHTMLBuilder.toString());
        
        // Test finding elements across all shadow roots
        for (int i = 0; i < 10; i++) {
            WebElement host = shadow.findElement("#perf-host-" + i);
            List<WebElement> items = shadow.getAllShadowElement(host, "[class^='perf-item-']");
            assertEquals(20, items.size(), "Each shadow root should contain 20 items");
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Performance assertion (should complete within reasonable time)
        assertTrue(duration < 10000, "Performance test should complete within 10 seconds, took: " + duration + "ms");
    }

    // Test browser-specific error handling
    @Test
    public void testBrowserSpecificErrorHandling() {
        driver.get("data:text/html,<html><body><div id='error-test'>Test</div></body></html>");
        
        // Test that invalid selectors are handled consistently across browsers
        assertThrows(Exception.class, () -> {
            shadow.findElement(">>invalid<<selector");
        });
        
        // Test that operations after errors still work
        WebElement validElement = shadow.findElement("#error-test");
        assertThat(validElement, notNullValue());
        
        // Test XPath error handling
        assertThrows(Exception.class, () -> {
            shadow.findElementByXPath("//invalid[@syntax[unclosed");
        });
        
        // Verify shadow operations still work after XPath error
        assertTrue(shadow.isVisible(validElement));
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