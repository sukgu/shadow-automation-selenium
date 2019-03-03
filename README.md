# Shadow root DOM automation using selenium

[![Build Status](https://travis-ci.org/sukgu/shadow-automation-selenium.svg?branch=master)](https://travis-ci.org/sukgu/shadow-automation-selenium "Travis CI")
[![codecov](https://codecov.io/gh/sukgu/shadow-automation-selenium/branch/master/graph/badge.svg)](https://codecov.io/gh/sukgu/shadow-automation-selenium)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cz.jirutka.rsql/rsql-parser/badge.svg)](https://oss.sonatype.org/#nexus-search;quick~io.github.sukgu)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Shadow DOM:
Shadow DOM is a web standard that offers component style and markup encapsulation. It is a critically important piece of the Web Components story as it ensures that a component will work in any environment even if other CSS or JavaScript is at play on the page.

## Problem Statement:
You have already developed your web-based automation framework in java selenium. Your frontend application uses Polymer that uses shadow dom. Selenium doesn't provide any way to deal with shadow-dom elements. 

## Solution:
You can use this plugin by adding jar file or by including maven dependency in your java selenium project.

## How it works:

## Methods:
  WebElement findElement(String cssSelector) : use this method if want single element from DOM

  List<WebElement> findElements(String cssSelector) : use this if you want to find all elements from DOM
  
###### How to use this plugin:
  You will have to add maven dependency in your pom file.
  
  ```
  <dependency>
	<groupId>io.github.sukgu</groupId>
	<artifactId>automation</artifactId>
	<version>0.0.1-SNAPSHOT</version>
  <dependency>
  ```
  Or you can download the jar file from repository https://oss.sonatype.org/content/repositories/snapshots/io/github/sukgu/automation/0.0.1-SNAPSHOT/
  
## Selector:
  ###### Examples: 
  for html tag ``` <paper-tab title="Settings"> ```
  You can use this code in your framework to grab the paper-tab element Object.
  ```java
    import io.github.sukgu;
	
	Shadow shadow = new Shadow(driver);
	WebElement element = shadow.findElement("paper-tab[title='Settings']");
	List<WebElement> element = shadow.findElements("paper-tab[title='Settings']");
    String text = element.getText();
  ```
  for html tag that resides under a shadow-root dom element ``` <input title="The name of the employee"> ```
  You can use this code in your framework to grab the paper-tab element Object.
  ```java
    import io.github.sukgu;
	
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
    import io.github.sukgu;
	
	Shadow shadow = new Shadow(driver);
	WebElement element = shadow.findElement("properties-page#settingsPage>textarea#textarea");
    String text = element.getText();
  ```
  
  ###### Note: > is used to combine multi level dom structure. So you can combine 3 levels of dom. If you want some more level modify the script and ready to rock.
  
