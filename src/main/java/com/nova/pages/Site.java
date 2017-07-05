package com.nova.pages;

import org.jbehave.web.selenium.WebDriverProvider;

import static org.openqa.selenium.By.id;
import static org.seleniumhq.selenium.fluent.Period.secs;

public class Site extends StepsPage {

    public Site(WebDriverProvider webDriverProvider) {
        super(webDriverProvider);
    }

    public int cartSize() {
        String cartSize = within(secs(2)).div(id("cart")).getText().toString().replace("Cart", "").trim();
        if (cartSize.equals("")) {
            return 0;
        }
        return Integer.parseInt(cartSize);
    }

}