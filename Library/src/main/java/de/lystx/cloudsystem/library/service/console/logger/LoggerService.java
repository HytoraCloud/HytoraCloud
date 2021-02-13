package de.lystx.cloudsystem.library.service.console.logger;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.console.color.ConsoleColor;
import de.lystx.cloudsystem.library.service.util.LogService;
import jline.console.ConsoleReader;
import lombok.Getter;
import org.fusesource.jansi.Ansi;

@Getter
public class LoggerService extends CloudService {

    private ConsoleReader consoleReader;


    public LoggerService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        try {
            this.consoleReader = new ConsoleReader(System.in, System.err);
            this.consoleReader.setExpandEvents(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String prefix, String message) {
        this.sendMessage("§7[§9" + prefix.toUpperCase() + "§7] §b" + message);
    }


    public void sendMessage(String message) {
        try {
            message = ConsoleColor.formatColorString(message);
            this.consoleReader.println(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + '\r' + ConsoleColor.formatColorString(message) + Ansi.ansi().reset().toString());
            this.consoleReader.drawLine();
            this.consoleReader.flush();

            LogService logService = this.getCloudLibrary().getService(LogService.class);
            if (logService != null) {
                logService.log(ConsoleColor.stripColor(message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
