package io.github.sukgu.pom;

import java.util.List;

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
	
	@FindElementBy(css = "#container>#inside")
	List<WebElement> levelInsides;
	
	@FindElementBy(css = "button")
	WebElement button;
	
	@FindElementBy(css = "div#divid>div#node>p")
	WebElement paragraph;
	
	@FindElementBy(css = "body")
	WebElement body;
	
	@FindElementBy(css = "body>div#divid>div#node>p1111")
	WebElement noSuchElement;
	
	@FindElementBy(xpath = "//body")
	WebElement bodyByXPath;
	
	@FindElementBy(xpath = "//body//div[1]")
	WebElement divByIndex;
	
	@FindElementBy(xpath = "//h3[text()='some DOM element']")
	WebElement h3ByXPathWithText;
	
	@FindElementBy(xpath = "//div[@id='container']//h2[text()='Inside Shadow DOM1'] | //div[@id='container']//h2[text()='Inside Shadow DOM']")
	WebElement h2XPathWithPipe;
	
	@FindElementBy(xpath = "//div[@id='container']//h2[text()='Inside Shadow DOM']")
	WebElement h3ByXPathWithTextInsideShadow;
	
	@FindElementBy(xpath = "//div[@id='container']//h2[text()='Inside Shadow DOM']")
	List<WebElement> h2AllElementsXPathWithText;
	
	@FindElementBy(xpath = "//div[@id='container']//h2[@id='inside']")
	List<WebElement> h2AllElementsXPathWithId;
	
	public LocalTestPage(WebDriver driver) {
		this.driver = driver;
		ElementFieldDecorator decorator = new ElementFieldDecorator(new DefaultElementLocatorFactory(driver));
		PageFactory.initElements(decorator, this);
	}

	public WebElement getContainer() {
		return container;
	}

	public void setContainer(WebElement element) {
		this.container = element;
	}

	public List<WebElement> getInsides() {
		return insides;
	}

	public void setInsides(List<WebElement> insides) {
		this.insides = insides;
	}

	public List<WebElement> getLevelInsides() {
		return levelInsides;
	}

	public void setLevelInsides(List<WebElement> levelInsides) {
		this.levelInsides = levelInsides;
	}

	public WebElement getButton() {
		return button;
	}

	public void setButton(WebElement button) {
		this.button = button;
	}

	public WebElement getParagraph() {
		return paragraph;
	}

	public void setParagraph(WebElement paragrapgh) {
		this.paragraph = paragrapgh;
	}

	public WebElement getBody() {
		return body;
	}

	public void setBody(WebElement body) {
		this.body = body;
	}

	public WebElement getNoSuchElement() {
		return noSuchElement;
	}

	public void setNoSuchElement(WebElement noSuchElement) {
		this.noSuchElement = noSuchElement;
	}

	public WebElement getBodyByXPath() {
		return bodyByXPath;
	}

	public void setBodyByXPath(WebElement bodyByXPath) {
		this.bodyByXPath = bodyByXPath;
	}

	public WebElement getDivByIndex() {
		return divByIndex;
	}

	public void setDivByIndex(WebElement divByIndex) {
		this.divByIndex = divByIndex;
	}

	public WebElement getH3ByXPathWithText() {
		return h3ByXPathWithText;
	}

	public void setH3ByXPathWithText(WebElement h3ByXPathWithText) {
		this.h3ByXPathWithText = h3ByXPathWithText;
	}

	public WebElement getH3ByXPathWithTextInsideShadow() {
		return h3ByXPathWithTextInsideShadow;
	}

	public void setH3ByXPathWithTextInsideShadow(WebElement h3ByXPathWithTextInsideShadow) {
		this.h3ByXPathWithTextInsideShadow = h3ByXPathWithTextInsideShadow;
	}

	public WebElement getH2XPathWithPipe() {
		return h2XPathWithPipe;
	}

	public void setH2XPathWithPipe(WebElement h2xPathWithPipe) {
		h2XPathWithPipe = h2xPathWithPipe;
	}

	public List<WebElement> getH2AllElementsXPathWithText() {
		return h2AllElementsXPathWithText;
	}

	public void setH2AllElementsXPathWithText(List<WebElement> h2AllElementsXPathWithText) {
		this.h2AllElementsXPathWithText = h2AllElementsXPathWithText;
	}

	public List<WebElement> getH2AllElementsXPathWithId() {
		return h2AllElementsXPathWithId;
	}

	public void setH2AllElementsXPathWithId(List<WebElement> h2AllElementsXPathWithId) {
		this.h2AllElementsXPathWithId = h2AllElementsXPathWithId;
	}

	public WebElement getH3() {
		return h3;
	}

	public void setH3(WebElement h3) {
		this.h3 = h3;
	}

	public List<WebElement> getAllH3() {
		return allH3;
	}

	public void setAllH3(List<WebElement> allH3) {
		this.allH3 = allH3;
	}
}
