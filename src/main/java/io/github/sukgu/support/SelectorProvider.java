package io.github.sukgu.support;

import io.github.sukgu.exceptions.UnsupportedSelector;
import org.openqa.selenium.By;

public class SelectorProvider {
    public static String buildSelector(By by) {
        String selector = extractSelector(by);

        if (by instanceof By.ByCssSelector) {
            return selector;
        } else if (by instanceof By.ByName) {
            return "[name='" + selector + "']";
        } else if (by instanceof By.ByXPath) {
            return selector;
        } else if (by instanceof By.ById) {
            return "#" + selector;
        } else if (by instanceof By.ByClassName) {
            return "." + selector;
        } else if (by instanceof By.ByTagName) {
            return selector;
        } else if (by instanceof By.ByLinkText) {
            return "//a[.='" + selector + "']";
        } else if (by instanceof By.ByPartialLinkText) {
            return "//a[contains(text(),'" + selector + "')]";
        }
        throw new UnsupportedSelector("Selector: " + selector + " is not supported yet.");
    }

    public static String extractSelector(By by) {
        return by.toString()
                .replaceAll("By.*: ", "")
                .replace("\"", "")
                .replace("\\", "\\\\");
    }
}


