package de.lystx.hytoracloud.driver.cloudservices.cloud.console.logger;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.CloudCompleter;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.color.ConsoleColor;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.utils.Utils;
import de.lystx.hytoracloud.driver.cloudservices.cloud.log.LogService;
import jline.console.ConsoleReader;
import lombok.Getter;

import java.io.IOException;
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
            try {
                this.consoleReader = new ConsoleReader(System.in, System.err);
                this.consoleReader.setExpandEvents(false);
                if (CloudDriver.getInstance() != null && CloudDriver.getInstance().getInstance(CommandService.class) != null) {
                    this.consoleReader.addCompleter(new CloudCompleter(CloudDriver.getInstance().getInstance(CommandService.class)));
                }
            } catch (IOException e) {
                System.out.println("[Console] Something went wrong while initialising ConsoleReader!");
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
        CloudDriver.getInstance().getInstance(LogService.class).log(prefix, message);
    }

    /**
     * Sends message to console
     *
     * @param message the message to send
     */
    public void sendMessage(String message) {
        try {
            message = ConsoleColor.formatColorString(message);
            this.consoleReader.println('\r' + message);
            this.consoleReader.drawLine();
            this.consoleReader.flush();

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

    @Override
    public void reload() {

    }

    @Override
    public void save() {

    }
}
