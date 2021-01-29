package de.lystx.cloudsystem.library.service.console;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.enums.ConsoleColor;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.util.LogService;
import jline.console.ConsoleReader;
import lombok.Getter;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.util.logging.Level;

@Getter
public class LoggerService extends CloudService {

    private ConsoleReader consoleReader;
    private final CloudLogger logger;


    public LoggerService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.logger = new CloudLogger();
        try {
            this.consoleReader = new ConsoleReader(System.in, System.err);
        } catch (Exception e) {}
        this.consoleReader.setExpandEvents(false);
    }

    public void sendMessage(String prefix, String message) {
        this.sendMessage("§7[§9" + prefix.toUpperCase() + "§7] §b" + message);
    }

    public void sendMessage() {
        this.sendMessage("§9");
    }

    public void sendMessage(String message) {
        try {
            message = this.colorString(message);
            try {
                this.consoleReader.println(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + '\r' + colorString(message) + Ansi.ansi().reset().toString());
                this.consoleReader.drawLine();
                this.consoleReader.flush();
            } catch (IOException exception) { }
            try {
                this.getCloudLibrary().getService(LogService.class).log(ConsoleColor.stripColor(message));
            } catch (NullPointerException e) {}
        } catch (Exception e) {}
    }

    public String readLine() {
        try {
            return this.consoleReader.readLine();
        } catch (IOException | StringIndexOutOfBoundsException ex) {
            this.logger.log(Level.SEVERE, "Something is wrong while reading Line", ex);
            return "null";
        }
    }

    public String readLine(String s) {
        try {
            return this.consoleReader.readLine(s);
        } catch (IOException | StringIndexOutOfBoundsException ex) {
            this.logger.log(Level.SEVERE, "Something is wrong while reading Line", ex);
            return "null";
        }
    }

    public ConsoleReader getConsoleReader() {
        return this.consoleReader;
    }

    public String colorString(String text) {
        for (ConsoleColor consoleColour : ConsoleColor.values()) {
            text = text.replace("§" + consoleColour.getIndex(), consoleColour.getAnsiCode());
        }
        return text;
    }


}
