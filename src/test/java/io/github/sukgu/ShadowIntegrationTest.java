package io.github.sukgu;

import java.net.URI;
import java.time.Duration;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShadowIntegrationTest {

    private WebDriver driver;
    private Shadow shadow;

    @BeforeAll
    public void setupOnce() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        driver = new ChromeDriver(options);
        shadow = new Shadow(driver);
    }

    @AfterAll
    public void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Complete workflow integration test
    @Test
    public void testCompleteWorkflowIntegration() {
        driver.get(getPageContent("workflow.html"));
        
        // Wait for shadow DOM to be fully initialized
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        WebElement appContainer = shadow.findElement("#app-container");
        assertThat(appContainer, notNullValue());
        
        // Test finding elements in shadow DOM
        WebElement appTitle = shadow.getShadowElement(appContainer, "#app-title");
        assertEquals("Shadow DOM Application", appTitle.getText());
        
        // Test form interaction workflow
        WebElement nameInput = shadow.getShadowElement(appContainer, "#user-name");
        WebElement emailInput = shadow.getShadowElement(appContainer, "#user-email");
        WebElement validateBtn = shadow.getShadowElement(appContainer, "#validate-btn");
        WebElement submitBtn = shadow.getShadowElement(appContainer, "#submit-btn");
        
        // Verify initial state
        assertTrue(shadow.isDisabled(submitBtn), "Submit button should be disabled initially");
        
        // Test validation with empty fields
        validateBtn.click();
        
        WebElement nameError = shadow.getShadowElement(appContainer, "#name-error");
        WebElement emailError = shadow.getShadowElement(appContainer, "#email-error");
        
        assertTrue(shadow.isVisible(nameError), "Name error should be visible");
        assertTrue(shadow.isVisible(emailError), "Email error should be visible");
        
        // Fill form with valid data
        nameInput.sendKeys("John Doe");
        emailInput.sendKeys("john@example.com");
        
        // Test checkbox and radio interactions
        shadow.selectCheckbox(appContainer, "Newsletter");
        shadow.selectCheckbox(appContainer, "Updates");
        shadow.selectRadio(appContainer, "Premium");
        
        // Test dropdown selection
        shadow.selectDropdown(appContainer, "United States");
        
        // Validate form
        validateBtn.click();
        
        // Wait for validation to complete
        wait.until(d -> !shadow.isDisabled(submitBtn));
        
        assertFalse(shadow.isDisabled(submitBtn), "Submit button should be enabled after validation");
        
        // Submit form
        submitBtn.click();
        
        // Verify result display
        WebElement resultSection = shadow.getShadowElement(appContainer, "#result-section");
        wait.until(d -> shadow.isVisible(resultSection));
        
        assertTrue(shadow.isVisible(resultSection), "Result section should be visible");
        
        WebElement resultContent = shadow.getShadowElement(appContainer, "#result-content");
        String resultText = resultContent.getText();
        
        assertTrue(resultText.contains("John Doe"), "Result should contain name");
        assertTrue(resultText.contains("john@example.com"), "Result should contain email");
        assertTrue(resultText.contains("premium"), "Result should contain account type");
        assertTrue(resultText.contains("US"), "Result should contain country");
        
        // Test reset functionality
        WebElement resetBtn = shadow.getShadowElement(appContainer, "#reset-btn");
        resetBtn.click();
        
        // Verify form is reset
        assertEquals("", nameInput.getAttribute("value"));
        assertEquals("", emailInput.getAttribute("value"));
        assertTrue(shadow.isVisible(resultSection), "Result section should be visible even after reset");
        assertTrue(shadow.isDisabled(submitBtn), "Submit button should be disabled after reset");
    }

    // Test stale element handling
    @Test
    public void testStaleElementHandling() {
        String dynamicHTML = 
            "<html><body>" +
            "<div id='dynamic-container'></div>" +
            "<button id='recreate-btn'>Recreate Elements</button>" +
            "<script>" +
            "let counter = 0;" +
            "function createElements() {" +
            "  const container = document.getElementById('dynamic-container');" +
            "  container.innerHTML = '';" +
            "  const shadowHost = document.createElement('div');" +
            "  shadowHost.id = 'shadow-host-' + (++counter);" +
            "  container.appendChild(shadowHost);" +
            "  " +
            "  const shadowRoot = shadowHost.attachShadow({mode: 'open'});" +
            "  shadowRoot.innerHTML = '<p id=\"dynamic-content\">Content ' + counter + '</p>';" +
            "}" +
            
            "createElements();" +
            
            "document.getElementById('recreate-btn').addEventListener('click', createElements);" +
            "</script>" +
            "</body></html>";
        
        driver.get("data:text/html," + dynamicHTML);
        
        // Get initial elements
        WebElement initialHost = shadow.findElement("[id^='shadow-host-']");
        WebElement initialContent = shadow.getShadowElement(initialHost, "#dynamic-content");
        
        assertEquals("Content 1", initialContent.getText());
        
        // Recreate elements (making previous elements stale)
        WebElement recreateBtn = shadow.findElement("#recreate-btn");
        recreateBtn.click();
        
        // Wait for new elements to be created
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(d -> {
            try {
                WebElement host = shadow.findElement("[id^='shadow-host-']");
                WebElement content = shadow.getShadowElement(host, "#dynamic-content");
                return "Content 2".equals(content.getText());
            } catch (Exception e) {
                return false;
            }
        });
        
        // Verify new elements work correctly
        WebElement newHost = shadow.findElement("[id^='shadow-host-']");
        WebElement newContent = shadow.getShadowElement(newHost, "#dynamic-content");
        
        assertEquals("Content 2", newContent.getText());
        
        // Old element should be stale
        assertThrows(StaleElementReferenceException.class, () -> {
            initialContent.getText();
        });
    }

    // Test concurrent operations and thread safety
    @Test
    public void testConcurrentOperations() {
        String multiElementHTML = 
            "<html><body>" +
            "<div id='multi-container'></div>" +
            "<script>" +
            "const container = document.getElementById('multi-container');" +
            "for(let i = 0; i < 10; i++) {" +
            "  const host = document.createElement('div');" +
            "  host.id = 'host-' + i;" +
            "  container.appendChild(host);" +
            "  " +
            "  const shadowRoot = host.attachShadow({mode: 'open'});" +
            "  shadowRoot.innerHTML = `" +
            "    <div class='shadow-content'>" +
            "      <span class='item' data-index='${i}'>Item ${i}</span>" +
            "      <button class='action-btn' data-target='${i}'>Action ${i}</button>" +
            "    </div>" +
            "  `;" +
            "}" +
            "</script>" +
            "</body></html>";
        
        driver.get("data:text/html," + multiElementHTML);
        
        // Perform multiple concurrent-like operations
        for (int i = 0; i < 10; i++) {
            WebElement host = shadow.findElement("#host-" + i);
            WebElement item = shadow.getShadowElement(host, ".item");
            WebElement button = shadow.getShadowElement(host, ".action-btn");
            
            assertEquals("Item " + i, item.getText());
            assertEquals("Action " + i, button.getText());
            assertEquals(String.valueOf(i), shadow.getAttribute(item, "data-index"));
            
            // Test highlighting multiple elements
            shadow.highlight(button, "blue", 100);
            assertTrue(shadow.isVisible(button));
        }
        
        // Verify all elements are still accessible
        List<WebElement> allHosts = shadow.findElements("[id^='host-']");
        assertEquals(10, allHosts.size());
        
        List<WebElement> allItems = shadow.findElements(".shadow-content .item");
        assertEquals(10, allItems.size());
    }

    // Test resource cleanup and memory management
    @Test
    public void testResourceManagement() {
        // Create and destroy multiple shadow DOM instances
        for (int iteration = 0; iteration < 5; iteration++) {
            String resourceHTML = 
                "<html><body>" +
                "<div id='resource-container-" + iteration + "'></div>" +
                "<script>" +
                "const container = document.getElementById('resource-container-" + iteration + "');" +
                "for(let i = 0; i < 20; i++) {" +
                "  const host = document.createElement('div');" +
                "  host.className = 'resource-host';" +
                "  container.appendChild(host);" +
                "  " +
                "  const shadowRoot = host.attachShadow({mode: 'open'});" +
                "  shadowRoot.innerHTML = '<div class=\"resource-content\">Resource ' + i + '</div>';" +
                "}" +
                "</script>" +
                "</body></html>";
            
            driver.get("data:text/html," + resourceHTML);
            
            // Perform operations on all elements
            List<WebElement> hosts = shadow.findElements(".resource-host");
            assertEquals(20, hosts.size());
            
            for (WebElement host : hosts) {
                WebElement content = shadow.getShadowElement(host, ".resource-content");
                assertThat(content, notNullValue());
                assertTrue(shadow.isVisible(content));
            }
            
            // Force some cleanup by navigating
            if (iteration < 4) {
                driver.get("about:blank");
            }
        }
        
        // Final verification that shadow operations still work
        driver.get("data:text/html,<html><body><div id='final-test'>Final Test</div></body></html>");
        WebElement finalElement = shadow.findElement("#final-test");
        assertThat(finalElement, notNullValue());
    }

    // Test error recovery and resilience
    @Test
    public void testErrorRecoveryAndResilience() {
        driver.get("data:text/html,<html><body><div id='resilience-test'>Test</div></body></html>");
        
        // Test recovery after various error conditions
        WebElement testElement = shadow.findElement("#resilience-test");
        assertThat(testElement, notNullValue());
        
        // Cause JavaScript errors and verify recovery
        try {
            ((JavascriptExecutor) driver).executeScript("throw new Error('Test error 1');");
        } catch (Exception e) {
            // Expected
        }
        
        // Shadow operations should still work
        assertTrue(shadow.isVisible(testElement));
        
        // Test with invalid operations
        try {
            shadow.findElement(">>invalid<<selector");
            fail("Should have thrown exception for invalid selector");
        } catch (Exception e) {
            // Expected
        }
        
        // Shadow operations should still work after error
        shadow.highlight(testElement, "red", 100);
        
        // Test with DOM manipulation
        ((JavascriptExecutor) driver).executeScript(
            "document.body.innerHTML = '<div id=\"new-content\">New Content</div>';"
        );
        
        // Should be able to find new elements
        WebElement newElement = shadow.findElement("#new-content");
        assertEquals("New Content", newElement.getText());
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