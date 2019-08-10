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
            appConfig.setConnectionString(prop.getProperty("connection_string"));
            appConfig.setUser(prop.getProperty("user"));
            appConfig.setPass(prop.getProperty("pass"));
            appConfig.setMem(new Boolean(prop.getProperty("is_mem")));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return appConfig;
    }
}
