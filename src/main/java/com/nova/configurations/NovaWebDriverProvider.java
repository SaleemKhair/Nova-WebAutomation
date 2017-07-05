package com.nova.configurations;

import org.jbehave.web.selenium.DelegatingWebDriverProvider;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NovaWebDriverProvider extends DelegatingWebDriverProvider {


    private static final Map<String, Supplier<WebDriver>> DRIVER_MAP = new HashMap<>();

    public void initialize() {
        registerDrivers();
        String driverName = NovaEnv.getEnv().getProperty("driver.name");
        delegate.set(DRIVER_MAP.get(driverName).get());
    }

    private void registerDrivers() {
        System.setProperty("webdriver.gecko.driver", getResource("Drivers/firefox/geckodriver.exe").getPath());
        System.setProperty("webdriver.chrome.driver", getResource("Drivers/chrome/chromedriver.exe").getPath());
        try {

            //TODO fix costume profile
            DRIVER_MAP.put("firefox", FirefoxDriver::new);
            DRIVER_MAP.put("chrome", () -> {
                ChromeOptions options = createChromeOptions();
                DesiredCapabilities cap = DesiredCapabilities.chrome();
                cap.setCapability(ChromeOptions.CAPABILITY, options);
                cap.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
                return new ChromeDriver(cap);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("test-type");
        options.addArguments("start-maximized");
        options.addArguments("--js-flags=--expose-gc");
        options.addArguments("--enable-precise-memory-info");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("test-type=browser");
        options.addArguments("disable-infobars");
        return options;
    }

    private URL getResource(String path) {
        return getClass().getClassLoader().getResource(path);
    }
}
