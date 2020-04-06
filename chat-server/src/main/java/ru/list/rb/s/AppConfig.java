package ru.list.rb.s;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {

    public static final String PROPERTIES_PATH_TO_HOST = "network.host";
    public static final String PROPERTIES_PATH_TO_PORT = "port";
//    public static final String PROPERTIES_PATH_TO_CLIENT_APP_TITLE = "client.app.title";
    private static volatile AppConfig instance;
    private static String host;
    private static int port;
//    private static String appTitle;
    private final static String PATH_TO_PROPERTIES = "application.properties";

    private AppConfig() {}

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(new File(PATH_TO_PROPERTIES)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        host = properties.getProperty(PROPERTIES_PATH_TO_HOST);
        port = Integer.parseInt(properties.getProperty(PROPERTIES_PATH_TO_PORT));
//        appTitle = properties.getProperty(PROPERTIES_PATH_TO_CLIENT_APP_TITLE);
        return instance;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

//    public String getAppTitle() {
//        return appTitle;
//    }
}
