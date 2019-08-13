import app.Configurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class.getCanonicalName());

    public static void main(String[] args) {
        LOGGER.info("Start main");
        Configurator configurator = new Configurator();
        configurator.start();
    }
}
