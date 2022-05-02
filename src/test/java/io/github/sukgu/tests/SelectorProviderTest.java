package io.github.sukgu.tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;

import static io.github.sukgu.support.SelectorProvider.buildSelector;
import static io.github.sukgu.support.SelectorProvider.extractSelector;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class SelectorProviderTest extends TestBase {

    @ParameterizedTest
    @MethodSource("io.github.sukgu.data.DataProvider#selectorsToExtract")
    public void correctSelector_shouldBeExtracted_whenProvideByClass(By by, String expectedSelector) {
        String sut = extractSelector(by);
        assertThat(sut, equalTo(expectedSelector));
    }

    @ParameterizedTest
    @MethodSource("io.github.sukgu.data.DataProvider#selectorsToBuild")
    public void correctSelector_shouldBeBuilt_whenProvideByClass(By by, String expectedSelector) {
        String sut = buildSelector(by);
        assertThat(sut, equalTo(expectedSelector));
    }
}