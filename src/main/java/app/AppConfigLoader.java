package app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfigLoader {

    public AppConfig load() {

        AppConfig appConfig = new AppConfig();

        try (InputStream input = AppConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            appConfig.setPort(prop.getProperty("port"));
            appConfig.setResource(prop.getProperty("resource"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return appConfig;
    }
}
