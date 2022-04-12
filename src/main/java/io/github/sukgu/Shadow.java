package io.github.sukgu;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class Shadow {
    private final String javascriptLibrary = convertJStoText().toString();
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor jse = null;
    private WebDriverException exception;
    private CommandExecutor executer;
    private SessionId sessionId;
    private int implicitWait = 0;
    private int explicitWait = 0;
    private int pollingTime = 0;

    public Shadow(WebDriver driver) {
        if (driver instanceof ChromeDriver) {
            sessionId = ((ChromeDriver) driver).getSessionId();
            jse = (JavascriptExecutor) driver;
        } else if (driver instanceof FirefoxDriver) {
            sessionId = ((FirefoxDriver) driver).getSessionId();
            jse = ((FirefoxDriver) driver);
        } else if (driver instanceof InternetExplorerDriver) {
            sessionId = ((InternetExplorerDriver) driver).getSessionId();
            jse = ((InternetExplorerDriver) driver);
        } else if (driver instanceof RemoteWebDriver) {
            sessionId = ((RemoteWebDriver) driver).getSessionId();
            jse = (JavascriptExecutor) driver;
        }
        this.driver = driver;
    }

    private Object injectShadowExecutor(String javascript) {
        if (jse == null) {
            return null;
        }
        waitForPageLoaded();
        return jse.executeScript(javascript);
    }

    private Object injectShadowExecutor(String javascript, WebElement element) {
        if (jse == null) {
            return null;
        }
        waitForPageLoaded();
        return jse.executeScript(javascript, element);
    }

    private Object executorGetObject(String script) {
        String javascript = javascriptLibrary + script;
        return injectShadowExecutor(javascript);
    }

    private Object executorGetObject(String script, WebElement element) {
        String javascript = javascriptLibrary + script;
        return injectShadowExecutor(javascript, element);
    }

    private StringBuilder convertJStoText() {
        StringBuilder text = new StringBuilder();
        try {
            InputStream in = getClass().getResourceAsStream("/querySelector.js");
            assert in != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            while (reader.ready()) {
                text.append(reader.readLine());
            }
            reader.close();
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
        }
        return text;
    }

    private void fixLocator(SearchContext context, String cssLocator, WebElement element) {
        if (element instanceof RemoteWebElement) {
            try {
                @SuppressWarnings("rawtypes")
                Class[] parameterTypes = new Class[]{SearchContext.class, String.class, String.class};
                Method m = element.getClass().getDeclaredMethod("setFoundBy", parameterTypes);
                m.setAccessible(true);
                Object[] parameters = new Object[]{context, "cssSelector", cssLocator};
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
                Class[] parameterTypes = new Class[]{SearchContext.class, String.class, String.class};
                Method m = element.getClass().getDeclaredMethod("setFoundBy", parameterTypes);
                m.setAccessible(true);
                Object[] parameters = new Object[]{context, "xpath", XPath};
                m.invoke(element, parameters);
            } catch (Exception fail) {
                //fail("Something bad happened when fixing locator");
            }
        }
    }

    private void waitForPageLoaded() {
        ExpectedCondition<Boolean> expectation = driver ->
                jse.executeScript("return document.readyState")
                        .toString()
                        .equals("complete");
        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
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
        if (pollingTime > seconds) {
            throw new Exception("pollingTime can't be greater than wait time");
        }
        this.explicitWait = seconds;
        this.pollingTime = pollingTime;
    }

    private boolean isPresent(WebElement element) {
        boolean present = false;
        try {
            present = (Boolean) executorGetObject("return isVisible(arguments[0]);", element);
        } catch (JavascriptException ignored) {
        }
        return present;
    }

    public WebElement findElement(String cssSelector) {
        WebElement element = null;
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
            element = (WebElement) executorGetObject(String.format("return getObject(\"%s\");", cssSelector));
            fixLocator(driver, cssSelector, element);
        }

        if (explicitWait > 0) {
            element = (WebElement) executorGetObject(String.format("return getObject(\"%s\");", cssSelector));
            fixLocator(driver, cssSelector, element);
            boolean visible = isPresent(element);

            for (int i = 0; i < explicitWait && !visible; ) {
                try {
                    Thread.sleep(pollingTime * 1000L);
                    element = (WebElement) executorGetObject(String.format("return getObject(\"%s\");", cssSelector));
                    fixLocator(driver, cssSelector, element);
                    visible = isPresent(element);
                    i += pollingTime;
                } catch (InterruptedException ignored) {
                }
            }
        }

        if (explicitWait == 0 && implicitWait == 0) {
            element = (WebElement) executorGetObject(String.format("return getObject(\"%s\");", cssSelector));
            fixLocator(driver, cssSelector, element);
        }

        if (element == null) {
            throw new NoSuchElementException(cssSelector);
        }

        return element;

    }

    public WebElement findElement(WebElement parent, String cssSelector) {
        WebElement element = null;
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
            element = (WebElement) executorGetObject("return getObject(\"" + cssSelector + "\", arguments[0]);", parent);
            fixLocator(driver, cssSelector, element);
        }

        if (explicitWait > 0) {
            element = (WebElement) executorGetObject("return getObject(\"" + cssSelector + "\", arguments[0]);", parent);
            fixLocator(driver, cssSelector, element);
            boolean visible = isPresent(element);

            for (int i = 0; i < explicitWait && !visible; ) {
                try {
                    Thread.sleep(pollingTime * 1000L);
                    element = (WebElement) executorGetObject("return getObject(\"" + cssSelector + "\", arguments[0]);", parent);
                    fixLocator(driver, cssSelector, element);
                    visible = isPresent(element);
                    i += pollingTime;
                } catch (InterruptedException ignored) {
                }
            }

        }

        if (explicitWait == 0 && implicitWait == 0) {
            element = (WebElement) executorGetObject("return getObject(\"" + cssSelector + "\", arguments[0]);", parent);
            fixLocator(driver, cssSelector, element);
        }

        if (element == null) {
            throw new NoSuchElementException(cssSelector);
        }

        return element;
    }

    @SuppressWarnings("unchecked")
    public List<WebElement> findElements(String cssSelector) {
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
        }
        List<WebElement> element = new LinkedList<>();
        Object object = executorGetObject("return getAllObject(\"" + cssSelector + "\");");
        if (object instanceof List<?>) {
            element = (List<WebElement>) object;
        }
        for (WebElement webElement : element) {
            fixLocator(driver, cssSelector, webElement);
        }
        return element;
    }

    @SuppressWarnings("unchecked")
    public List<WebElement> findElements(WebElement parent, String cssSelector) {
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
        }
        List<WebElement> element = new LinkedList<>();
        Object object = executorGetObject("return getAllObject(\"" + cssSelector + "\", arguments[0]);", parent);
        if (object instanceof List<?>) {
            element = (List<WebElement>) object;
        }
        for (WebElement webElement : element) {
            fixLocator(driver, cssSelector, webElement);
        }
        return element;
    }

    private WebElement elementByXPath(String XPath) {
        WebElement element = null;
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
            element = (WebElement) executorGetObject(String.format("return getXPathObject(\"%s\");", XPath));
            fixLocatorXPath(driver, XPath, element);
        }

        if (explicitWait > 0) {
            element = (WebElement) executorGetObject(String.format("return getXPathObject(\"%s\");", XPath));
            fixLocatorXPath(driver, XPath, element);
            boolean visible = isPresent(element);

            for (int i = 0; i < explicitWait && !visible; ) {
                try {
                    Thread.sleep(pollingTime * 1000L);
                    element = (WebElement) executorGetObject(String.format("return getXPathObject(\"%s\");", XPath));
                    fixLocatorXPath(driver, XPath, element);
                    visible = isPresent(element);
                    i += pollingTime;
                } catch (InterruptedException ignored) {
                }
            }
        }

        if (explicitWait == 0 && implicitWait == 0) {
            element = (WebElement) executorGetObject(String.format("return getXPathObject(\"%s\");", XPath));
            fixLocatorXPath(driver, XPath, element);
        }

        return element;
    }

    private WebElement elementByXPath(WebElement parent, String XPath) {
        WebElement element = null;
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
            element = (WebElement) executorGetObject("return getXPathObject(\"" + XPath + "\", arguments[0]);", parent);
            fixLocatorXPath(driver, XPath, element);
        }

        if (explicitWait > 0) {
            element = (WebElement) executorGetObject("return getXPathObject(\"" + XPath + "\", arguments[0]);", parent);
            fixLocatorXPath(driver, XPath, element);
            boolean visible = isPresent(element);

            for (int i = 0; i < explicitWait && !visible; ) {
                try {
                    Thread.sleep(pollingTime * 1000L);
                    element = (WebElement) executorGetObject("return getXPathObject(\"" + XPath + "\", arguments[0]);", parent);
                    fixLocatorXPath(driver, XPath, element);
                    visible = isPresent(element);
                    i += pollingTime;
                } catch (InterruptedException ignored) {
                }
            }

        }

        if (explicitWait == 0 && implicitWait == 0) {
            element = (WebElement) executorGetObject("return getXPathObject(\"" + XPath + "\", arguments[0]);", parent);
            fixLocatorXPath(driver, XPath, element);
        }

        return element;
    }

    public WebElement findElementByXPath(String XPath) {
        List<WebElement> elements = new LinkedList<>();
        Arrays.asList(XPath.split(Pattern.quote("|"))).forEach(x_path -> {
            WebElement element = elementByXPath(x_path.trim());
            if (element != null) {
                elements.add(element);
            }
        });
        if (elements.size() > 0) {
            return elements.get(0);
        }
        throw new NoSuchElementException(XPath);
    }

    public WebElement findElementByXPath(WebElement parent, String XPath) {
        List<WebElement> elements = new LinkedList<>();
        Arrays.asList(XPath.split(Pattern.quote("|"))).forEach(x_path -> {
            WebElement element = elementByXPath(parent, x_path.trim());
            if (element != null) {
                elements.add(element);
            }
        });
        if (elements.size() > 0) {
            return elements.get(0);
        }
        throw new NoSuchElementException(XPath);
    }

    @SuppressWarnings("unchecked")
    private List<WebElement> elementsByXPath(String XPath) {
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
        }
        List<WebElement> element = null;
        Object object = executorGetObject("return getXPathAllObject(\"" + XPath + "\");");
        if (object instanceof List<?>) {
            element = (List<WebElement>) object;
        }
        for (WebElement webElement : element) {
            fixLocatorXPath(driver, XPath, webElement);
        }
        return element;
    }

    @SuppressWarnings("unchecked")
    private List<WebElement> elementsByXPath(WebElement parent, String XPath) {
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
        }
        List<WebElement> element = null;
        Object object = executorGetObject("return getXPathAllObject(\"" + XPath + "\", arguments[0]);", parent);
        if (object instanceof List<?>) {
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
            if (elementList.size() > 0) {
                elements.add(elementList);
            }
        });
        if (elements.size() > 0) {
            return elements.get(0);
        }
        return new ArrayList<>();
    }

    public List<WebElement> findElementsByXPath(WebElement parent, String XPath) {
        List<List<WebElement>> elements = new LinkedList<>();
        Arrays.asList(XPath.split(Pattern.quote("|"))).forEach(x_path -> {
            List<WebElement> elementList = elementsByXPath(parent, x_path.trim());
            if (elementList.size() > 0) {
                elements.add(elementList);
            }
        });
        if (elements.size() > 0) {
            return elements.get(0);
        }
        return new ArrayList<>();
    }

    public WebElement getShadowElement(WebElement parent, String selector) {
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
        }
        WebElement element = null;
        element = (WebElement) executorGetObject("return getShadowElement(arguments[0],\"" + selector + "\");", parent);
        fixLocator(driver, selector, element);
        return element;
    }

    @SuppressWarnings("unchecked")
    public List<WebElement> getAllShadowElement(WebElement parent, String selector) {
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
        }
        List<WebElement> elements = null;
        Object object = executorGetObject("return getAllShadowElement(arguments[0],\"" + selector + "\");", parent);
        if (object instanceof List<?>) {
            elements = (List<WebElement>) object;
        }
        for (WebElement element : elements) {
            fixLocator(driver, selector, element);
        }
        return elements;
    }

    public WebElement getParentElement(WebElement element) {
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
        }
        return (WebElement) executorGetObject("return getParentElement(arguments[0]);", element);
    }

    @SuppressWarnings("unchecked")
    public List<WebElement> getChildElements(WebElement parent) {
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
        }
        List<WebElement> elements = null;
        Object object = executorGetObject("return getChildElements(arguments[0]);", parent);
        if (object instanceof List<?>) {
            elements = (List<WebElement>) object;
        }
        return elements;
    }

    @SuppressWarnings("unchecked")
    public List<WebElement> getSiblingElements(WebElement element) {
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
        }
        List<WebElement> elements = null;
        Object object = executorGetObject("return getSiblingElements(arguments[0]);", element);
        if (object instanceof List<?>) {
            elements = (List<WebElement>) object;
        }
        return elements;
    }

    public WebElement getSiblingElement(WebElement element, String selector) {
        if (implicitWait > 0) {
            try {
                Thread.sleep(implicitWait * 1000L);
            } catch (InterruptedException ignored) {
            }
        }
        return (WebElement) executorGetObject("return getSiblingElement(arguments[0],\"" + selector + "\");", element);
    }

    public WebElement getNextSiblingElement(WebElement element) {
        return (WebElement) executorGetObject("return getNextSiblingElement(arguments[0]);", element);
    }

    public WebElement getPreviousSiblingElement(WebElement element) {
        return (WebElement) executorGetObject("return getPreviousSiblingElement(arguments[0]);", element);
    }

    public boolean isVisible(WebElement element) {
        return (Boolean) executorGetObject("return isVisible(arguments[0]);", element);
    }

    public boolean isChecked(WebElement element) {
        return (Boolean) executorGetObject("return isChecked(arguments[0]);", element);
    }

    public boolean isDisabled(WebElement element) {
        return (Boolean) executorGetObject("return isDisabled(arguments[0]);", element);
    }

    public String getAttribute(WebElement element, String attribute) {
        return (String) executorGetObject("return getAttribute(arguments[0],\"" + attribute + "\");", element);
    }

    public void selectCheckbox(WebElement parentElement, String label) {
        executorGetObject("return selectCheckbox(\"" + label + "\",arguments[0]);", parentElement);
    }

    public void selectCheckbox(String label) {
        executorGetObject("return selectCheckbox(\"" + label + "\");");
    }

    public void selectRadio(WebElement parentElement, String label) {
        executorGetObject("return selectRadio(\"" + label + "\",arguments[0]);", parentElement);
    }

    public void selectRadio(String label) {
        executorGetObject("return selectRadio(\"" + label + "\");");
    }

    public void selectDropdown(WebElement parentElement, String label) {
        executorGetObject("return selectDropdown(\"" + label + "\",arguments[0]);", parentElement);
    }

    public void selectDropdown(String label) {
        executorGetObject("return selectDropdown(\"" + label + "\");");
    }

    public void scrollTo(WebElement element) {
        executorGetObject("return scrollTo(arguments[0]);", element);
    }

    private void applyStyle(WebElement element, String style) {
        jse.executeScript("arguments[0].setAttribute('style', '" + style + "');", element);
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
        int time = 3000;

        highlight(element, color, time);
    }


}
