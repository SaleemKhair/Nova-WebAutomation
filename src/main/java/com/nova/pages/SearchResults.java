package com.nova.pages;

import org.jbehave.web.selenium.WebDriverProvider;
import org.openqa.selenium.WebElement;
import org.seleniumhq.selenium.fluent.FluentMatcher;
import org.seleniumhq.selenium.fluent.FluentWebElement;
import org.seleniumhq.selenium.fluent.FluentWebElements;

import static org.openqa.selenium.By.className;
import static org.openqa.selenium.By.xpath;
import static org.seleniumhq.selenium.fluent.Period.secs;

public class SearchResults extends StepsPage {

    public SearchResults(WebDriverProvider webDriverProvider) {
        super(webDriverProvider);
    }

    public String buyFirst(String thing) {
        getResultElements().first(lowerCaseTitleContaining(thing)).click();
        int ix = getCurrentUrl().indexOf("/listing/") + 9;
        String id = getCurrentUrl().substring(ix, ix + 8);
        // id.isNumber().shouldBe true, "no listing found";
        input(xpath("@value = 'Add to Cart'")).click();
        return id;
    }

    public  int resultsFound() {
        return getResultElements().size();
    }


    private FluentWebElements getResultElements() {
        return within(secs(2)).links(className("listing-thumb"));
    }

    private FluentMatcher lowerCaseTitleContaining(final String thing) {
        return new FluentMatcher() {
            public boolean matches(FluentWebElement fluentWebElement, int i) {
                return fluentWebElement.getAttribute("title").toString().toLowerCase().contains(thing);
            }





        };
    }
}