package de.lystx.hytoracloud.driver.utils.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.AllArgsConstructor;

/**
 * This class disables unused
 * loggings for like netty or mongodb
 * or anything you want to disable
 * just add it to the {@link java.lang.reflect.Constructor} and
 * use the {@link Loggers#disable()} Method to disable them
 */
@AllArgsConstructor
public class Loggers {

    private final LoggerContext loggerContext;
    private final String[] loggers;

    /**
     * Disables Logging for given classes using LoggerContext
     */
    public void disable() {
        for (String logger : this.loggers) {
            Logger log = this.loggerContext.getLogger(logger);
            log.setLevel(Level.OFF);
        }

    }
}
