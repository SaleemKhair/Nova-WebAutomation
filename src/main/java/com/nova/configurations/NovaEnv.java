package com.nova.configurations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by Saleem on 30/06/2017.
 */
public class NovaEnv {

    private static final Properties CONFIG = new Properties();
    private static NovaEnv INSTANCE = new NovaEnv();

    private NovaEnv() {

    }

    public synchronized static NovaEnv getEnv() {
        if (INSTANCE == null)
            return new NovaEnv();
        return INSTANCE;

    }

    public String getProperty(String key) {
        return CONFIG.getProperty(key);
    }


    static {
        URL configurationsFile = getFileResource("configurations/configurations.properties");
        try (InputStream is = new FileInputStream(configurationsFile.getFile())) {
            CONFIG.load(is);
        } catch (FileNotFoundException e) {
            //TODO implement logger
        } catch (IOException e) {
            //TODO implement logger
        }
    }


    private static URL getFileResource(String path) {
        ClassLoader classLoader = NovaWebDriverProvider.class.getClassLoader();
        URL resource = classLoader.getResource(path);
        if (resource == null)
            throw new RuntimeException();
        return resource;
    }
}
