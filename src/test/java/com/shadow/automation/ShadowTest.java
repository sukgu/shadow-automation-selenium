package com.shadow.automation;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ShadowTest {
	
	private final static ChromeDriver driver = new ChromeDriver();
	private static Shadow shadow = null; 
	
	@BeforeAll
	static void injectShadowJS() {
		System.out.println("qaqaqaqaqaqaqaq a qa qaqaqaqaq");
		driver.navigate().to("https://www.virustotal.com");
		shadow = new Shadow(driver);
	}
	
	@BeforeEach
    void init() {
		
    }
    
	@Test
    void testApp()
    {
        
    }
	
	@Test
	void testJSInjection() {
		WebElement element = shadow.findElement("a[data-route='url']");
		System.out.println(element);
		//Assertions.assertEquals(new String(""), shadow.driver.getPageSource(), "Message");
	}
	
	@Test
	void testGetAllObject() {
		List<WebElement> element = shadow.findElements("a[data-route='url']");
		System.out.println(element);
	}
	
	@AfterEach
    void tearDown() {
    }

    @AfterAll
    static void tearDownAll() {
    	driver.close();
    }
}
