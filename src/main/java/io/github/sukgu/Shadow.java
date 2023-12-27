package io.github.sukgu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
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
	RemoteWebDriver remoteWebDriver;
	private final String javascriptLibrary = convertJStoText().toString();
	private int implicitWait = 0;
	private int explicitWait = 0;
	private int pollingTime = 0;
	
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
		} else if (driver instanceof RemoteWebDriver) {
			sessionId  = ((RemoteWebDriver)driver).getSessionId();
			remoteWebDriver = (RemoteWebDriver)driver;
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
		} else if (remoteWebDriver!=null) {
			JavascriptExecutor js = (JavascriptExecutor)remoteWebDriver;
			waitForPageLoaded();
			return js.executeScript(javascript);
		} else {
			return null;
		}
	}
	
	private Object injectShadowExecuter(String javascript, WebElement element) {
		if(chromeDriver!=null) {
			JavascriptExecutor js = (JavascriptExecutor)chromeDriver;
			waitForPageLoaded();
			return js.executeScript(javascript, element);
		} else if (firfoxDriver!=null) {
			waitForPageLoaded();
			return firfoxDriver.executeScript(javascript, element);
		} else if (ieDriver!=null) {
			waitForPageLoaded();
			return ieDriver.executeScript(javascript, element);
		} else if (remoteWebDriver!=null) {
			JavascriptExecutor js = (JavascriptExecutor)remoteWebDriver;
			waitForPageLoaded();
			return js.executeScript(javascript, element);
		} else {
			return null;
		}
	}
	
	private Object executerGetObject(String script) {
		String javascript = javascriptLibrary;
		javascript += script;
		return injectShadowExecuter(javascript);
	}
	
	private Object executerGetObject(String script, WebElement element) {
		String javascript = javascriptLibrary;
		javascript += script;
		return injectShadowExecuter(javascript, element);
	}
	
	private StringBuilder convertJStoText() {
		InputStream in = getClass().getResourceAsStream("/querySelector.js"); 
		BufferedReader reader = null;
		//File jsFile = new File("querySelector.js");
		//BufferedReader reader = null;
		StringBuilder text = new StringBuilder();
		//reader = new BufferedReader(new FileReader(jsFile));
		reader = new BufferedReader(new InputStreamReader(in)); 
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
	
	private void fixLocatorXPath(SearchContext context, String XPath, WebElement element) {
		if (element instanceof RemoteWebElement) {
			try {
			@SuppressWarnings("rawtypes")
			Class[] parameterTypes = new Class[] { SearchContext.class,
			        String.class, String.class };
			Method m = element.getClass().getDeclaredMethod(
			        "setFoundBy", parameterTypes);
			m.setAccessible(true);
			Object[] parameters = new Object[] { context, "xpath", XPath };
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
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(expectation);
        } catch (Throwable error) {
            //Assertions.fail("Timeout waiting for Page Load Request to complete.");
        }
    }
	
	// wait methods on shadow objects
	public void setImplicitWait(int seconds) {
        this.implicitWait = seconds;
    }
	
	public void setExplicitWait(int seconds, int pollingTime) throws Exception {
		if(pollingTime > seconds) {
			throw new Exception("pollingTime can't be greater than wait time");
		}
        this.explicitWait = seconds;
        this.pollingTime = pollingTime;
    }
	
	private boolean isPresent(WebElement element) {
		boolean present = false;
		try {
			present =  (Boolean) executerGetObject("return isVisible(arguments[0]);", element);
		} catch(JavascriptException ex) {
			
		}
		return present;
	}
	
	public WebElement findElement(String cssSelector) {
		WebElement element = null;
		boolean visible = false;
		
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
			element = (WebElement) executerGetObject(String.format("return getObject(\"%s\");", cssSelector));
			fixLocator(driver, cssSelector, element);
			visible = isPresent(element);
		}
		
		if(explicitWait > 0) {
			element = (WebElement) executerGetObject(String.format("return getObject(\"%s\");", cssSelector));
			fixLocator(driver, cssSelector, element);
			visible = isPresent(element);
			
			for(int i = 0 ; i < explicitWait && !visible;) {
				try {
					Thread.sleep(pollingTime * 1000);
					element = (WebElement) executerGetObject(String.format("return getObject(\"%s\");", cssSelector));
					fixLocator(driver, cssSelector, element);
					visible = isPresent(element);
					i = i + pollingTime;
				} catch (InterruptedException e) {
					
				}
			}
		}
		
		if(explicitWait == 0 && implicitWait == 0) {
			element = (WebElement) executerGetObject(String.format("return getObject(\"%s\");", cssSelector));
			fixLocator(driver, cssSelector, element);
		}
		
		if (element == null) {
			throw new NoSuchElementException(cssSelector);
		}
		
		return element;

	}
	
	public WebElement findElement(WebElement parent, String cssSelector) {
		WebElement element = null;
		boolean visible = false;
		
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
			element = (WebElement) executerGetObject("return getObject(\""+cssSelector+"\", arguments[0]);", parent);
			fixLocator(driver, cssSelector, element);
			visible = isPresent(element);
		}
		
		if(explicitWait > 0) {
			element = (WebElement) executerGetObject("return getObject(\""+cssSelector+"\", arguments[0]);", parent);
			fixLocator(driver, cssSelector, element);
			visible = isPresent(element);
			
			for(int i = 0 ; i < explicitWait && !visible;) {
				try {
					Thread.sleep(pollingTime * 1000);
					element = (WebElement) executerGetObject("return getObject(\""+cssSelector+"\", arguments[0]);", parent);
					fixLocator(driver, cssSelector, element);
					visible = isPresent(element);
					i = i + pollingTime;
				} catch (InterruptedException e) {
					
				}
			}
			
		}
		
		if(explicitWait == 0 && implicitWait == 0) {
			element = (WebElement) executerGetObject("return getObject(\""+cssSelector+"\", arguments[0]);", parent);
			fixLocator(driver, cssSelector, element);
		}
		
		if (element == null) {
			throw new NoSuchElementException(cssSelector);
		}
		
		return element;
	}
	
	@SuppressWarnings("unchecked")
	public List<WebElement> findElements(String cssSelector) {
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
		}
		List<WebElement> element = new LinkedList<>();
		Object object = executerGetObject("return getAllObject(\""+cssSelector+"\");");
		if(object != null && object instanceof List<?>) {
			element = (List<WebElement>) object;
		}
		for (WebElement webElement : element) {
			fixLocator(driver, cssSelector, webElement);
		}
		return element;
	}
	
	@SuppressWarnings("unchecked")
	public List<WebElement> findElements(WebElement parent, String cssSelector) {
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
		}
		List<WebElement> element = new LinkedList<>();
		Object object = executerGetObject("return getAllObject(\""+cssSelector+"\", arguments[0]);", parent);
		if(object != null && object instanceof List<?>) {
			element = (List<WebElement>) object;
		}
		for (WebElement webElement : element) {
			fixLocator(driver, cssSelector, webElement);
		}
		return element;
	}
	
	private WebElement elementByXPath(String XPath) {
		WebElement element = null;
		boolean visible = false;
		
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
			element = (WebElement) executerGetObject(String.format("return getXPathObject(\"%s\");", XPath));
			fixLocatorXPath(driver, XPath, element);
			visible = isPresent(element);
		}
		
		if(explicitWait > 0) {
			element = (WebElement) executerGetObject(String.format("return getXPathObject(\"%s\");", XPath));
			fixLocatorXPath(driver, XPath, element);
			visible = isPresent(element);
			
			for(int i = 0 ; i < explicitWait && !visible;) {
				try {
					Thread.sleep(pollingTime * 1000);
					element = (WebElement) executerGetObject(String.format("return getXPathObject(\"%s\");", XPath));
					fixLocatorXPath(driver, XPath, element);
					visible = isPresent(element);
					i = i + pollingTime;
				} catch (InterruptedException e) {
					
				}
			}
		}
		
		if(explicitWait == 0 && implicitWait == 0) {
			element = (WebElement) executerGetObject(String.format("return getXPathObject(\"%s\");", XPath));
			fixLocatorXPath(driver, XPath, element);
		}
		
		return element;
	}
	
	private WebElement elementByXPath(WebElement parent, String XPath) {
		WebElement element = null;
		boolean visible = false;
		
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
			element = (WebElement) executerGetObject("return getXPathObject(\""+XPath+"\", arguments[0]);", parent);
			fixLocatorXPath(driver, XPath, element);
			visible = isPresent(element);
		}
		
		if(explicitWait > 0) {
			element = (WebElement) executerGetObject("return getXPathObject(\""+XPath+"\", arguments[0]);", parent);
			fixLocatorXPath(driver, XPath, element);
			visible = isPresent(element);
			
			for(int i = 0 ; i < explicitWait && !visible;) {
				try {
					Thread.sleep(pollingTime * 1000);
					element = (WebElement) executerGetObject("return getXPathObject(\""+XPath+"\", arguments[0]);", parent);
					fixLocatorXPath(driver, XPath, element);
					visible = isPresent(element);
					i = i + pollingTime;
				} catch (InterruptedException e) {
					
				}
			}
			
		}
		
		if(explicitWait == 0 && implicitWait == 0) {
			element = (WebElement) executerGetObject("return getXPathObject(\""+XPath+"\", arguments[0]);", parent);
			fixLocatorXPath(driver, XPath, element);
		}
		
		return element;
	}
	
	public WebElement findElementByXPath(String XPath) {
		List<WebElement> elements = new LinkedList<>();
		Arrays.asList(XPath.split(Pattern.quote("|"))).forEach(x_path -> {
			WebElement element = elementByXPath(x_path.trim());
			if(element != null) {
				elements.add(element);
			}
		});
		if(elements.size() > 0) {
			return elements.get(0);
		}
		throw new NoSuchElementException(XPath);
	}
	
	public WebElement findElementByXPath(WebElement parent, String XPath) {
		List<WebElement> elements = new LinkedList<>();
		Arrays.asList(XPath.split(Pattern.quote("|"))).forEach(x_path -> {
			WebElement element = elementByXPath(parent, x_path.trim());
			if(element != null) {
				elements.add(element);
			}
		});
		if(elements.size() > 0) {
			return elements.get(0);
		}
		throw new NoSuchElementException(XPath);
	}
	
	@SuppressWarnings("unchecked")
	private List<WebElement> elementsByXPath(String XPath) {
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
		}
		List<WebElement> element = null;
		Object object = executerGetObject("return getXPathAllObject(\""+XPath+"\");");
		if(object != null && object instanceof List<?>) {
			element = (List<WebElement>) object;
		}
		for (WebElement webElement : element) {
			fixLocatorXPath(driver, XPath, webElement);
		}
		return element;
	}
	
	@SuppressWarnings("unchecked")
	private List<WebElement> elementsByXPath(WebElement parent, String XPath) {
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
		}
		List<WebElement> element = null;
		Object object = executerGetObject("return getXPathAllObject(\""+XPath+"\", arguments[0]);", parent);
		if(object != null && object instanceof List<?>) {
			element = (List<WebElement>) object;
		}
		for (WebElement webElement : element) {
			fixLocatorXPath(driver, XPath, webElement);
		}
		return element;
	}
	
	public List<WebElement> findElementsByXPath(String XPath) {
		List<List<WebElement>> elements = new LinkedList<>();
		Arrays.asList(XPath.split(Pattern.quote("|"))).forEach(x_path -> {
			List<WebElement> elementList = elementsByXPath(x_path.trim());
			if(elementList.size() > 0) {
				elements.add(elementList);
			}
		});
		if(elements.size() > 0) {
			return elements.get(0);
		}
		return new ArrayList<>();
	}
	
	public List<WebElement> findElementsByXPath(WebElement parent, String XPath) {
		List<List<WebElement>> elements = new LinkedList<>();
		Arrays.asList(XPath.split(Pattern.quote("|"))).forEach(x_path -> {
			List<WebElement> elementList = elementsByXPath(parent, x_path.trim());
			if(elementList.size() > 0) {
				elements.add(elementList);
			}
		});
		if(elements.size() > 0) {
			return elements.get(0);
		}
		return new ArrayList<>();
	}
	
	public WebElement getShadowElement(WebElement parent,String selector) {
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
		}
		WebElement element = null;
		element = (WebElement) executerGetObject("return getShadowElement(arguments[0],\""+selector+"\");", parent);
		fixLocator(driver, selector, element);
		return element;
	}
	
	@SuppressWarnings("unchecked")
	public List<WebElement> getAllShadowElement(WebElement parent,String selector) {
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
		}
		List<WebElement> elements = null;
		Object object = executerGetObject("return getAllShadowElement(arguments[0],\""+selector+"\");", parent);
		if(object!=null && object instanceof List<?>) {
			elements = (List<WebElement>) object;
		}
		for (WebElement element : elements) {
			fixLocator(driver, selector, element);
		}
		return elements;
	}

	public WebElement getParentElement(WebElement element) {
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
		}
		return (WebElement) executerGetObject("return getParentElement(arguments[0]);", element);
	}
	
	@SuppressWarnings("unchecked")
	public List<WebElement> getChildElements(WebElement parent) {
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
		}
		List<WebElement> elements = null;
		Object object = executerGetObject("return getChildElements(arguments[0]);", parent);
		if(object != null && object instanceof List<?>) {
			elements = (List<WebElement>) object;
		}
		return elements;
	}
	
	@SuppressWarnings("unchecked")
	public List<WebElement> getSiblingElements(WebElement element) {
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
		}
		List<WebElement> elements = null;
		Object object = executerGetObject("return getSiblingElements(arguments[0]);", element);
		if(object != null && object instanceof List<?>) {
			elements = (List<WebElement>) object;
		}
		return elements;
	}
	
	public WebElement getSiblingElement(WebElement element, String selector) {
		if(implicitWait > 0) {
			try {
				Thread.sleep(implicitWait * 1000);
			} catch (InterruptedException e) {
				
			}
		}
		return (WebElement) executerGetObject("return getSiblingElement(arguments[0],\""+selector+"\");", element);
	}
	
	public WebElement getNextSiblingElement(WebElement element) {
		return (WebElement) executerGetObject("return getNextSiblingElement(arguments[0]);", element);
	}
	
	public WebElement getPreviousSiblingElement(WebElement element) {
		return (WebElement)  executerGetObject("return getPreviousSiblingElement(arguments[0]);", element);
	}
	
	public boolean isVisible(WebElement element) {
		return (Boolean) executerGetObject("return isVisible(arguments[0]);", element);
	}
	
	public boolean isChecked(WebElement element) {
		return (Boolean) executerGetObject("return isChecked(arguments[0]);", element);
	}
	
	public boolean isDisabled(WebElement element) {
		return (Boolean) executerGetObject("return isDisabled(arguments[0]);", element);
	}
	
	public String getAttribute(WebElement element,String attribute) {
		return (String) executerGetObject("return getAttribute(arguments[0],\""+attribute+"\");", element);
	}
	
	public void selectCheckbox(WebElement parentElement,String label) {
		executerGetObject("return selectCheckbox(\""+label+"\",arguments[0]);", parentElement);
	}
	
	public void selectCheckbox(String label) {
		executerGetObject("return selectCheckbox(\""+label+"\");");
	}
	
	public void selectRadio(WebElement parentElement,String label) {
		executerGetObject("return selectRadio(\""+label+"\",arguments[0]);", parentElement);
	}
	
	public void selectRadio(String label) {
		executerGetObject("return selectRadio(\""+label+"\");");
	}
	
	public void selectDropdown(WebElement parentElement,String label) {
		executerGetObject("return selectDropdown(\""+label+"\",arguments[0]);", parentElement);
	}
	
	public void selectDropdown(String label) {
		executerGetObject("return selectDropdown(\""+label+"\");");
	}
	
	public void scrollTo(WebElement element) {
		executerGetObject("return scrollTo(arguments[0]);", element);
	}
	
	private void applyStyle(WebElement element, String style) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('style', '"+style+"');", element);
	}
	
	public void highlight(WebElement element, String color, Integer timeInMiliSeconds) {
		long time = timeInMiliSeconds == null ? 4000 : timeInMiliSeconds;
		String border = "3";
	    String originalStyle = element.getAttribute("style");
	    applyStyle(element, String.format("border: %spx solid %s;", border, color));
	    try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    applyStyle(element, originalStyle);
	}
	
	public void highlight(WebElement element) {
		String color = "red";
		long time = 3000;
		String border = "3";
	    String originalStyle = element.getAttribute("style");
	    applyStyle(element, String.format("border: %spx solid %s;", border, color));
	    try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    applyStyle(element, originalStyle);
	}
	
	
}
