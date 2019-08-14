package app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class AppConfigLoader {

    public AppConfig load() {

        AppConfig appConfig = new AppConfig();

        try (InputStream input = AppConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            appConfig.setPort(prop.getProperty("port"));
            appConfig.setResource(prop.getProperty("resource"));
            appConfig.setConnectionString(prop.getProperty("connection_string"));
            appConfig.setUser(prop.getProperty("user"));
            appConfig.setPass(prop.getProperty("pass"));
            appConfig.setMem(Boolean.valueOf(prop.getProperty("is_mem")));
            appConfig.setMaxIdle(Integer.valueOf(prop.getProperty("max_idle")));
            appConfig.setMaxIdle(Integer.valueOf(prop.getProperty("min_idle")));
            appConfig.setMaxIdle(Integer.valueOf(prop.getProperty("max_statements")));

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return appConfig;
    }
}
