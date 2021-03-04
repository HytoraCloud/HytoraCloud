package de.lystx.cloudsystem.library.service.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.AllArgsConstructor;

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
