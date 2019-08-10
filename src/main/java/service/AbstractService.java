package service;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractService {
    private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

    public void log(Level level, String text) {
        logger.log(level, text);
    }
}
