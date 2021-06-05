package de.lystx.hytoracloud.driver.service.console.logger;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.main.ICloudService;
import de.lystx.hytoracloud.driver.service.command.CommandService;
import de.lystx.hytoracloud.driver.service.console.CloudCompleter;
import de.lystx.hytoracloud.driver.service.console.color.ConsoleColor;
import de.lystx.hytoracloud.driver.service.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.util.Utils;
import de.lystx.hytoracloud.driver.service.util.log.LogService;
import jline.console.ConsoleReader;
import lombok.Getter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@ICloudServiceInfo(
        name = "LoggerService",
        type = CloudServiceType.UTIL,
        description = {
                "This service is used for the CLoudConsole to read everything"
        },
        version = 1.3
)
public class LoggerService implements ICloudService {

    private ConsoleReader consoleReader;

    public LoggerService() {
        if (!CloudDriver.getInstance().isNeedsDependencies()) {
            try {
                this.consoleReader = new ConsoleReader(System.in, System.err);
                this.consoleReader.setExpandEvents(false);
                if (!CloudDriver.getInstance().isJlineCompleterInstalled()) {
                    if (CloudDriver.getInstance() != null && CloudDriver.getInstance().getInstance(CommandService.class) != null) {
                        this.consoleReader.addCompleter(new CloudCompleter(CloudDriver.getInstance().getInstance(CommandService.class)));
                    }
                }
            } catch (IOException e) {
                System.out.println("[Console] Something went wrong while initialising ConsoleReader!");
            }
        } else {
            System.out.println("[Console] Couldn't find JLine dependency!");
        }
    }

    /**
     * Sends message with prefix
     *
     * @param prefix the prefix of the message
     * @param message the message
     */
    public void sendMessage(String prefix, String message) {
        this.sendMessage("§h[§7" + Utils.getSimpleDateFormat().format(new Date()) + " §7| §b" + prefix.toUpperCase() + "§h] §f" + message);
    }

    /**
     * Sends message to console
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        try {
            message = ConsoleColor.formatColorString(message);
            if (!CloudDriver.getInstance().isNeedsDependencies()) {
                this.consoleReader.println('\r' + message);
                this.consoleReader.drawLine();
                this.consoleReader.flush();
            } else {
                System.out.println(ConsoleColor.stripColor(message));
            }
            try {
                LogService logService = this.getDriver().getInstance(LogService.class);
                if (logService != null) logService.log(ConsoleColor.stripColor(message));
            } catch (NullPointerException e) {
                //IGNORING
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
