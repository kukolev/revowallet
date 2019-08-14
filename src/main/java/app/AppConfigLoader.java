package app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static util.MapperUtils.str;

class AppConfigLoader {

    private static final Logger LOGGER = LogManager.getLogger(AppConfigLoader.class.getCanonicalName());

    public AppConfig load() {
        LOGGER.debug("Start load");
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
            LOGGER.error(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        LOGGER.debug("Finish load: appConfig = ", str(appConfig));
        return appConfig;
    }
}
