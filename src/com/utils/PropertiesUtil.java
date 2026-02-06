package com.utils;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private static final Properties props = new Properties();

    static {
        try (InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream("framework.properties")) {
            if (is != null) {
                props.load(is);
            } else {
                System.err.println(".properties non trouvé dans le classpath.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
