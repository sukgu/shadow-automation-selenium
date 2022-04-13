package io.github.sukgu;

import io.github.sukgu.exceptions.UnsupportedSelector;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

public class ShadowDriver extends Shadow implements WebDriver, JavascriptExecutor {
    public ShadowDriver(WebDriver driver) {
        super(driver);
    }

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
    public List<WebElement> findElements(By by) {
        String selector = getSelector(by);

        if (by instanceof By.ByCssSelector) {
            return findElements(selector);
        } else if (by instanceof By.ByName) {
            return findElements("[name=" + selector + "\"]");
        } else if (by instanceof By.ByXPath) {
            return findElementsByXPath(selector);
        } else if (by instanceof By.ById) {
            return findElements("#" + selector);
        } else if (by instanceof By.ByClassName) {
            return findElements("[class=" + selector + "]");
        }
        throw new UnsupportedSelector("Selector: " + selector + " is not supported yet.");
    }

    @Override
    public WebElement findElement(By by) {
        String selector = getSelector(by);

        if (by instanceof By.ByCssSelector) {
            return findElement(selector);
        } else if (by instanceof By.ByName) {
            return findElement("[name=" + selector + "\"]");
        } else if (by instanceof By.ByXPath) {
            return findElementByXPath(selector);
        } else if (by instanceof By.ById) {
            return findElement("#" + selector);
        } else if (by instanceof By.ByClassName) {
            return findElement("[class=" + selector + "]");
        }
        throw new UnsupportedSelector("Selector: " + selector + " is not supported yet.");
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

    public String getSelector(By by) {
        return by.toString().replaceAll("By.*: ", "");
    }

    @Override
    public Object executeScript(String s, Object... objects) {
        return jse.executeScript(s, objects);
    }

    @Override
    public Object executeAsyncScript(String s, Object... objects) {
        return jse.executeAsyncScript(s, objects);
    }
}
