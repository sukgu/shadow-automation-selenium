# Shadow root DOM automation using selenium

[![Build Status](https://travis-ci.org/sukgu/shadow-automation-selenium.svg?branch=master)](https://travis-ci.org/sukgu/shadow-automation-selenium "Travis CI")
[![codecov](https://codecov.io/gh/sukgu/shadow-automation-selenium/branch/master/graph/badge.svg)](https://codecov.io/gh/sukgu/shadow-automation-selenium)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.sukgu/automation.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.sukgu%22%20AND%20a:%22automation%22)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Shadow DOM:
Shadow DOM is a web standard that offers component style and markup encapsulation. It is a critically important piece of the Web Components story as it ensures that a component will work in any environment even if other CSS or JavaScript is at play on the page.

## Custom HTML Tags:
Custom HTML tags can't be directly identified with selenium tools. Using this plugin you can handle any custom HTML tags.

## Problem Statement:
- You have already developed your web-based automation framework in java selenium. Your frontend application uses Polymer that uses shadow dom. Selenium doesn't provide any way to deal with shadow-dom elements.
- Your application page contains custom HTML tags that can't be identified directly using selenium.

## Solution:
You can use this plugin by adding jar file or by including maven dependency in your java selenium project.

## How it works:

## Methods:
  `WebElement findElement(String cssSelector)` : use this method if want single element from DOM

  `List<WebElement> findElements(String cssSelector)` : use this if you want to find all elements from DOM
  
  `WebElement findElement(WebElement parent, String cssSelector)` : use this if you want to find a single elements from parent object DOM
  
  `List<WebElement> findElements(WebElement parent, String cssSelector)` : use this if you want to find all elements from parent object DOM
  
  `WebElement findElementByXPath(String XPath)` : use this method if want single element from DOM

  `List<WebElement> findElementsByXPath(String XPath)` : use this if you want to find all elements from DOM
  
  `WebElement findElementByXPath(WebElement parent, String XPath)` : use this if you want to find a single elements from parent object DOM
  
  `List<WebElement> findElementsByXPath(WebElement parent, String XPath)` : use this if you want to find all elements from parent object DOM
  
   `void setImplicitWait(int seconds)` : use this method for implicit wait
    
   `void setExplicitWait(int seconds, int pollingTime) throws Exception` : use this method for explicit wait
  
  `WebElement getShadowElement(WebElement parent,String selector)` : use this if you want to find a single element from parent DOM
  
  `List<WebElement> getAllShadowElement(WebElement parent,String selector)` : use this if you want to find all elements from parent DOM
  
  `WebElement getParentElement(WebElement element)` : use this to get the parent element if web element.
  
  `List<WebElement> getChildElements(WebElement parent)` : use this to get all the child elements of parent element.
  
  `List<WebElement> getSiblingElements(WebElement element)` : use this to get all adjacent (sibling) elements.
  
  `WebElement getSiblingElement(WebElement element, String selector)` : use this to get adjacent(sibling) element using css selector.
  
  `WebElement getNextSiblingElement(WebElement element)` : use this to get next adjacent(sibling) element.
  
  `WebElement getPreviousSiblingElement(WebElement element)` : use this to get previous adjacent(sibling) element..
  
  `boolean isVisible(WebElement element)` : use this if you want to find visibility of element
  
  `boolean isChecked(WebElement element)` : use this if you want to check if checkbox is selected 
  
  `boolean isDisabled(WebElement element)` : use this if you want to check if element is disabled
  
  `String getAttribute(WebElement element,String attribute)` : use this if you want to get attribute like aria-selected and other custom attributes of elements.
  
  `void selectCheckbox(String label)` : use this to select checkbox element using label.
  
  `void selectCheckbox(WebElement parentElement, String label)` : use this to select checkbox element using label.
  
  `void selectRadio(String label)` : use this to select radio element using label.
  
  `void selectRadio(WebElement parentElement, String label)` : use this to select radio element from parent DOM using label.
  
  `void selectDropdown(String label)` : use this to select dropdown list item using label (use this if only one dropdown is present or loaded on UI).
  
  `void selectDropdown(WebElement parentElement, String label)` : use this to select dropdown list item from parent DOM using label.
  
  `void scrollTo(WebElement element)` : use this to scroll to web element.
  
  `public void highlight(WebElement element, String color, Integer timeInMiliSeconds)` : highlight method.
  
  `public void highlight(WebElement element)` : highlight method highlight in red color.

### What's New 
##### We support now Selenium version 4 with release of Shadow version 0.1.5
##### Java 11 and Selenium 4.16.1 to be used with Shadow 0.1.5

###### How to use this plugin:
  You will have to dependency in your project.
  
  **Maven**
  ```
  <dependency>
	<groupId>io.github.sukgu</groupId>
	<artifactId>automation</artifactId>
	<version>0.1.5</version>
  </dependency>
  ```
  
  **Gradle**
  ```
  implementation 'io.github.sukgu:automation:0.1.5'
  ```
  
  
 You can download the jar file from repository http://central.maven.org/maven2/io/github/sukgu/automation/0.1.5/automation-0.1.5.jar
  
## Selector:
  ###### Examples: 
  for html tag ``` <paper-tab title="Settings"> ```
  You can use this code in your framework to grab the paper-tab element Object.
  ```java
    import io.github.sukgu.*;
	
	Shadow shadow = new Shadow(driver);
	WebElement element = shadow.findElement("paper-tab[title='Settings']");
	List<WebElement> element = shadow.findElements("paper-tab[title='Settings']");
    String text = element.getText();
  ```
  for html tag that resides under a shadow-root dom element ``` <input title="The name of the employee"> ```
  You can use this code in your framework to grab the paper-tab element Object.
  ```java
    import io.github.sukgu.*;
	
	Shadow shadow = new Shadow(driver);
	WebElement element = shadow.findElement("input[title='The name of the employee']");
    String text = element.getText();
  ```
  for html tag that resides under a shadow-root dom element 
  ``` 
  <properties-page id="settingsPage"> 
    <textarea id="textarea">
  </properties-page>
  ```
  You can use this code in your framework to grab the textarea element Object.
  ```java
    import io.github.sukgu.*;
	
	Shadow shadow = new Shadow(driver);
	WebElement element = shadow.findElement("properties-page#settingsPage>textarea#textarea");
    String text = element.getText();
  ```
  
## Note for XPath:

* 🟩 The findElementByXPath or findElementsByXPath takes XPath only with double slash internally for intermediate selections
* 🟩 //div[@id='container']//h2[text()='Inside Shadow DOM'] is **correct**
* 🟩 //div[@id='container']/h2[text()='Inside Shadow DOM'] is **correct**
* 🟩 For examples on XPath follow the [link](https://github.com/sukgu/shadow-automation-selenium/wiki/Examples-for-XPath-selector)
  
## Wait: Implicit and Explicit
If you want to use wait to synchronize your scripts then you should use the implicit or explicit wait feature.

* For Implicit wait, you can use **shadow.setImplicitWait(int seconds)** method.
* For Explicit wait, you can use **shadow.setExplicitWait(int seconds, int pollingTime)** method.

* In Implicit wait, the driver will wait for at least n seconds as set in **shadow.setImplicitWait(n)**.
* In Explicit wait, the driver will wait for at max n seconds as set in **shadow.setImplicitWait(n,m)**. In between driver will check for presence of WebElement every m seconds.

## PageFactory:
* @FindElementBy annotation can be used with PageFactory model to find elements based on css_selector or xpath.
* To achieve this you will need to modify page initialization method as `PageFactory.initElements(new ElementFieldDecorator(new DefaultElementLocatorFactory(driver), this)`.
* For more example on PageFactory see this [page](https://github.com/sukgu/shadow-automation-selenium/wiki/PageFactory-Annotations).
#### PageFactory Example:
``` java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import io.github.sukgu.support.ElementFieldDecorator;
import io.github.sukgu.support.FindElementBy;

public class LocalTestPage {
	
	WebDriver driver;
	
	@FindElementBy(css = "#container")
	WebElement container;
	
	@FindBy(css = "#h3")
	WebElement h3;
	
	@FindBy(css = "#h3")
	List<WebElement> allH3;
	
	@FindElementBy(css = "#inside")
	List<WebElement> insides;
	
	@FindElementBy(xpath = "//body")
	WebElement bodyByXPath;
	
	@FindElementBy(xpath = "//body//div[1]")
	WebElement divByIndex;
	
    public LocalTestPage(WebDriver driver) {
    	this.driver = driver;
    	ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
    	// need to use decorator if you want to use @FindElementBy in your PageFactory model.
    	PageFactory.initElements(decorator, this);
    }
    //... 
}
```
  
  **Documentation** [Link](https://github.com/sukgu/shadow-automation-selenium/wiki)
  
  **Library for Python Selenium** [Link](https://pypi.org/project/pyshadow/)
  **Git Repo for Python Selenium** [Link](https://github.com/sukgu/pyshadow/)
  
