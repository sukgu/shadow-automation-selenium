package io.github.sukgu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Shadow {
	
	WebDriver driver;
	WebDriverException exception;
	WebDriverWait wait;
	CommandExecutor executer;
	SessionId sessionId;
	ChromeDriver chromeDriver;
	FirefoxDriver firfoxDriver;
	InternetExplorerDriver ieDriver;
	
	public Shadow(WebDriver driver) {
		
		if (driver instanceof ChromeDriver) {
			sessionId  = ((ChromeDriver)driver).getSessionId();
			chromeDriver = (ChromeDriver)driver;
		} else if (driver instanceof FirefoxDriver) {
			sessionId  = ((FirefoxDriver)driver).getSessionId();
			firfoxDriver = (FirefoxDriver)driver;
		} else if (driver instanceof InternetExplorerDriver) {
			sessionId  = ((InternetExplorerDriver)driver).getSessionId();
			ieDriver = (InternetExplorerDriver)driver;
		}
		this.driver = driver;
	}
	
	private Object injectShadowExecuter(String javascript) {
		if(chromeDriver!=null) {
			JavascriptExecutor js = (JavascriptExecutor)chromeDriver;
			waitForPageLoaded();
			return js.executeScript(javascript);
		} else if (firfoxDriver!=null) {
			waitForPageLoaded();
			return firfoxDriver.executeScript(javascript);
		} else if (ieDriver!=null) {
			waitForPageLoaded();
			return ieDriver.executeScript(javascript);
		} else {
			return null;
		}
	}
	
	private Object executerGetObject(String script) {
		String javascript = convertJStoText().toString();
		javascript += script;
		return injectShadowExecuter(javascript);
	}
	
	private StringBuilder convertJStoText() {
		File jsFile = new File("resource/querySelector.js");
		BufferedReader reader = null;
		StringBuilder text = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(jsFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		if(reader!=null) {
			try {
				while(reader.ready()) {
					text.append(reader.readLine());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(reader!=null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return text;
	}
	
	private void fixLocator(SearchContext context, String cssLocator, WebElement element) {
		if (element instanceof RemoteWebElement) {
			try {
			@SuppressWarnings("rawtypes")
			Class[] parameterTypes = new Class[] { SearchContext.class,
			        String.class, String.class };
			Method m = element.getClass().getDeclaredMethod(
			        "setFoundBy", parameterTypes);
			m.setAccessible(true);
			Object[] parameters = new Object[] { context, "cssSelector", cssLocator };
			m.invoke(element, parameters);
			} catch (Exception fail) {
				//fail("Something bad happened when fixing locator");
			}
		}
	}
	
	private void waitForPageLoaded() {
        ExpectedCondition<Boolean> expectation = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
                    }
                };
        try {
            Thread.sleep(1000);
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(expectation);
        } catch (Throwable error) {
            //Assertions.fail("Timeout waiting for Page Load Request to complete.");
        }
    }
	
	public WebElement findElement(String cssSelector) {
		WebElement element = null;
		element = (WebElement) executerGetObject("return getObject(\""+cssSelector+"\");");
		fixLocator(driver, cssSelector, element);
		return element;
	}
	
	@SuppressWarnings("unchecked")
	public List<WebElement> findElements(String cssSelector) {
		List<WebElement> element = null;
		Object object = executerGetObject("return getAllObject(\""+cssSelector+"\");");
		if(object!=null && object instanceof List<?>) {
			element = (List<WebElement>) object;
		}
		for (WebElement webElement : element) {
			fixLocator(driver, cssSelector, webElement);
		}
		return element;
	}

}
