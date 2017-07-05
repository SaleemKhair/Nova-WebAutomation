package com.nova.pages;

import org.jbehave.web.selenium.FluentWebDriverPage;
import org.jbehave.web.selenium.WebDriverProvider;

/**
 * Created by Saleem on 18/06/2017.
 * For Shared Steps between all Steps Classes
 */
public class StepsPage  extends FluentWebDriverPage {
    public StepsPage(WebDriverProvider driverProvider) {
        super(driverProvider);
    }
}
