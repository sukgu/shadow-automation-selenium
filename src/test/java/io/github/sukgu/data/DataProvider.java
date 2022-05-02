package io.github.sukgu.data;

import org.junit.jupiter.params.provider.Arguments;
import org.openqa.selenium.By;

import java.util.stream.Stream;

public class DataProvider {
    //region SelectorProviderTest data
    public static Stream<Arguments> selectorsToExtract() {
        return Stream.of(
                Arguments.of(By.cssSelector("div > span.test"), "div > span.test"),
                Arguments.of(By.name("someName"), "someName"),
                Arguments.of(By.xpath("//a[starts-with(@href, '/')]"), "//a[starts-with(@href, '/')]"),
                Arguments.of(By.id("someId"), "someId"),
                Arguments.of(By.className("someClass"), "someClass"),
                Arguments.of(By.tagName("footer"), "footer"),
                Arguments.of(By.linkText("Some text"), "Some text"),
                Arguments.of(By.partialLinkText("Part of some text"), "Part of some text")
        );
    }

    public static Stream<Arguments> selectorsToBuild() {
        return Stream.of(
                Arguments.of(By.cssSelector("div > span.test"), "div > span.test"),
                Arguments.of(By.name("someName"), "[name='someName']"),
                Arguments.of(By.xpath("//a[starts-with(@href, '/')]"), "//a[starts-with(@href, '/')]"),
                Arguments.of(By.id("someId"), "#someId"),
                Arguments.of(By.className("someClass"), ".someClass"),
                Arguments.of(By.tagName("footer"), "footer"),
                Arguments.of(By.linkText("Some text"), "//a[.='Some text']"),
                Arguments.of(By.partialLinkText("Part of some text"), "//a[contains(text(),'Part of some text')]")
        );
    }
    //endregion

    //region ShadowDriverTest data
    public static Stream<Arguments> findElementSelectors() {
        return Stream.of(
                Arguments.of(By.cssSelector("[data-route=url]")),
                Arguments.of(By.name("form.suspicious-dns")),
                Arguments.of(By.xpath("//span[@id='wrapperLink']")),
                Arguments.of(By.id("wrapperLink")),
                Arguments.of(By.className("about")),
                Arguments.of(By.tagName("body")),
                Arguments.of(By.partialLinkText("Check our")),
                Arguments.of(By.linkText("Check our API"))
        );
    }

    public static Stream<Arguments> findElementsSelectors() {
        return Stream.of(
                Arguments.of(By.cssSelector("[data-route=url]")),
                Arguments.of(By.name("trigger")),
                Arguments.of(By.xpath("//*[contains(@id,'-feature')]")),
                Arguments.of(By.id("wrapperLink")),
                Arguments.of(By.className("about")),
                Arguments.of(By.tagName("div")),
                Arguments.of(By.partialLinkText("API")),
                Arguments.of(By.linkText("Check our API"))
        );
    }
    //endregion
}
