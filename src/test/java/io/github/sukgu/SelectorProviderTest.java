package io.github.sukgu;

import io.github.sukgu.support.SelectorProvider;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class SelectorProviderTest extends SelectorProvider {

    @ParameterizedTest
    @MethodSource("selectorsToExtractSet")
    public void correctSelector_shouldBeExtracted_whenProvideByClass(By by, String expectedSelector) {
        String sut = extractSelector(by);
        assertThat(sut, equalTo(expectedSelector));
    }

    @ParameterizedTest
    @MethodSource("selectorsToBuildSet")
    public void correctSelector_shouldBeBuilt_whenProvideByClass(By by, String expectedSelector) {
        String sut = buildSelector(by);
        assertThat(sut, equalTo(expectedSelector));
    }

    private static Stream<Arguments> selectorsToExtractSet() {
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

    private static Stream<Arguments> selectorsToBuildSet() {
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
}