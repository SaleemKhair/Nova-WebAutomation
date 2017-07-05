package com.nova.pages;

import org.jbehave.web.selenium.WebDriverProvider;

import static org.openqa.selenium.By.className;

public class Treasury extends StepsPage {

    public Treasury(WebDriverProvider webDriverProvider) {
        super(webDriverProvider);
    }

    public void chooseFirstGallery() {
        div(className("item-treasury-info-box")).h3().link().click();
    }

}
