package de.lystx.cloudsystem.launcher;

import ch.qos.logback.classic.LoggerContext;
import de.lystx.cloudsystem.CloudSystem;
import de.lystx.cloudsystem.other.Loggers;
import org.fusesource.jansi.AnsiConsole;
import org.slf4j.LoggerFactory;

public class Bootstrap {


    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        Loggers loggers = new Loggers((LoggerContext) LoggerFactory.getILoggerFactory(), new String[]{"io.netty", "org.mongodb.driver"});
        loggers.disable();

        CloudSystem cloudSystem = new CloudSystem();
    }
}
