package de.lystx.cloudsystem.library.service.console.logger;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.console.CloudCompleter;
import de.lystx.cloudsystem.library.service.console.color.ConsoleColor;
import de.lystx.cloudsystem.library.service.util.Constants;
import de.lystx.cloudsystem.library.service.util.LogService;
import jline.console.ConsoleReader;
import lombok.Getter;

import java.io.IOException;

@Getter
public class LoggerService extends CloudService {

    private ConsoleReader consoleReader;


    public LoggerService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        if (!Constants.NEEDS_DEPENDENCIES) {
            try {
                this.consoleReader = new ConsoleReader(System.in, System.err);
                this.consoleReader.setExpandEvents(false);
                if (!Constants.NEEDS_DEPENDENCIES_2) {
                    //this.consoleReader.addCompleter(new CloudCompleter(cloudLibrary.getService(CommandService.class)));
                }
            } catch (IOException e) {
                System.out.println("[Console] Something went wrong while initialising ConsoleReader!");
            }
        } else {
            System.out.println("[Console] Couldn't find JLine dependency!");
        }
    }

    public void sendMessage(String prefix, String message) {
        this.sendMessage("§7[§9" + prefix.toUpperCase() + "§7] §b" + message);
    }


    public void sendMessage(String message) {
        try {
            message = ConsoleColor.formatColorString(message);
            if (!Constants.NEEDS_DEPENDENCIES) {
                this.consoleReader.println('\r' + message);
                this.consoleReader.drawLine();
                this.consoleReader.flush();
            } else {
                System.out.println(ConsoleColor.stripColor(message));
            }

            LogService logService = this.getCloudLibrary().getService(LogService.class);
            if (logService != null) {
                logService.log(ConsoleColor.stripColor(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
