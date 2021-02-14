package de.lystx.cloudsystem.other;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Loggers {

    private final LoggerContext loggerContext;
    private final String[] loggers;

    public void disable() {
        for (String logger : this.loggers) {
            Logger log = this.loggerContext.getLogger(logger);
            log.setLevel(Level.OFF);
        }

    }
}
