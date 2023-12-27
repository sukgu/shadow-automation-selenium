package io.github.sukgu.support;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import io.github.sukgu.Shadow;
import io.github.sukgu.support.FindElementBy.FindByBuilder.SelectorType;

abstract class BaseBy extends By {

    protected final String selector;
    protected final String selectorType;

    protected BaseBy(String selector, String selectorType) {
        this.selector = selector;
        this.selectorType = selectorType;
    }

    protected final WebDriver getWebDriver(SearchContext context) {
        WebDriver webDriver;
        
        if (context instanceof RemoteWebElement) {
        	webDriver = ((RemoteWebElement) context).getWrappedDriver();
        } else {
        	webDriver = (WebDriver) context;
        }
        
        return webDriver;
    }

	@Override
    public WebElement findElement(SearchContext context) {
    	WebDriver webDriver = getWebDriver(context);
    	
        if (context instanceof WebDriver) {
            context = null;
        }
        
        Shadow shadow = new Shadow(webDriver);
        
        if(selectorType.equals(SelectorType.CSS_SELECTOR.toString())) {
        	return shadow.findElement(selector);
        }
        
        if(selectorType.equals(SelectorType.XPATH.toString())) {
        	return shadow.findElementByXPath(selector);
        }
        
        return null;
    }

	@Override
    public List<WebElement> findElements(SearchContext context) {
		WebDriver webDriver = getWebDriver(context);
		
        if (context instanceof WebDriver) {
            context = null;
        }
        
        Shadow shadow = new Shadow(webDriver);
        if(selectorType.equals(SelectorType.CSS_SELECTOR.toString())) {
        	return shadow.findElements(selector);
        }
        
        if(selectorType.equals(SelectorType.XPATH.toString())) {
        	return shadow.findElementsByXPath(selector);
        }
        
        return null;
    }
}
