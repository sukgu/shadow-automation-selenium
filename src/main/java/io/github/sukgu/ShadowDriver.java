package io.github.sukgu;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static io.github.sukgu.support.SelectorProvider.buildSelector;

public class ShadowDriver extends Shadow implements WebDriver, JavascriptExecutor, HasInputDevices, Interactive, TakesScreenshot {
    public ShadowDriver(WebDriver driver) {
        super(driver);
    }

    @Override
    public List<WebElement> findElements(By by) {
        String selector = buildSelector(by);
        if (by instanceof By.ByXPath || by instanceof By.ByLinkText || by instanceof By.ByPartialLinkText) {
            return findElementsByXPath(selector);
        }
        return findElements(selector);
    }

    @Override
    public WebElement findElement(By by) {
        String selector = buildSelector(by);
        if (by instanceof By.ByXPath || by instanceof By.ByLinkText || by instanceof By.ByPartialLinkText) {
            return findElementByXPath(selector);
        }
        return findElement(selector);
    }
    
    //region WebDriver delegates
    @Override
    public void get(String url) {
        driver.get(url);
    }

    @Override
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver.getTitle();
    }

    @Override
    public String getPageSource() {
        return driver.getPageSource();
    }

    @Override
    public void close() {
        driver.close();
    }

    @Override
    public void quit() {
        driver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return driver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return driver.navigate();
    }

    @Override
    public Options manage() {
        return driver.manage();
    }
    //endregion

    //region JavascriptExecutor delegates
    @Override
    public Object executeScript(String s, Object... objects) {
        return jse.executeScript(s, objects);
    }

    @Override
    public Object executeAsyncScript(String s, Object... objects) {
        return jse.executeAsyncScript(s, objects);
    }
    //endregion

    //region HasInputDevices delegates
    @Override
    public Keyboard getKeyboard() {
        return ((HasInputDevices) driver).getKeyboard();
    }

    @Override
    public Mouse getMouse() {
        return ((HasInputDevices) driver).getMouse();
    }
    //endregion

    //region Interactive delegates
    @Override
    public void perform(Collection<Sequence> collection) {
        ((Interactive) driver).perform(collection);
    }

    @Override
    public void resetInputState() {
        ((Interactive) driver).resetInputState();
    }
    //endregion

    //region TakesScreenshot delegates
    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
        return ((TakesScreenshot) driver).getScreenshotAs(outputType);
    }
    //endregion
}
