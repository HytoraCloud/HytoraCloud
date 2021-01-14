package de.lystx.cloudsystem.library.service.console;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CloudLogger extends Logger {

    public CloudLogger() {
        super("CloudLogger", null);
        setLevel(Level.SEVERE);
    }
}
