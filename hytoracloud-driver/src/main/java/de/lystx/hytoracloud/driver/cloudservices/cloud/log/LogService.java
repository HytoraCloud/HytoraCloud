package de.lystx.hytoracloud.driver.cloudservices.cloud.log;

import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.global.config.FileService;
import de.lystx.hytoracloud.driver.cloudservices.cloud.console.CloudConsole;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class logs all contents
 * of the {@link CloudConsole} to
 * the given file for the current log
 * it will cache the log and writes it on shutdown.
 */
@Getter @Setter
@ICloudServiceInfo(
        name = "LogService",
        type = CloudServiceType.CONFIG,
        description = {
                "This service is used to log everything and save the logs later"
        },
        version = 1.0
)
public class LogService implements ICloudService {

    /**
     * All the cached logs
     */
    private final List<String> logs;

    /**
     * The logfile
     */
    private File logFile;

    /**
     * If active
     */
    private boolean active;

    public LogService() {
        this.logs = new LinkedList<>();
        this.active = true;
        this.newFile();
    }

    /**
     * Logs something to the cache
     *
     * @param prefix the prefix
     * @param message the message
     */
    public void log(String prefix, String message) {
        this.log("[" + prefix.toUpperCase() + "] " + message);
    }

    /**
     * Raw logging method
     *
     * @param message the message
     */
    public void log(String message) {
        if (!this.isActive()) {
            return;
        }
        this.logs.add(message);
    }

    @Override
    public void reload() {

    }

    /**
     * Saves current log
     */
    public void save() {
        try {
            for (String log : this.logs) {
                this.getDriver().getInstance(FileService.class).write(this.logFile, log);
            }
            this.newFile();
        } catch (NullPointerException | ConcurrentModificationException ignored) {
            //Exception thrown sometimes but doesn't make sense
        }
    }

    /**
     * Creates new file if not exists
     */
    public void newFile() {
        this.logFile = new File(this.getDriver().getInstance(FileService.class).getLogsDirectory(), (new SimpleDateFormat("dd.MM.yyyy_HH_mm_ss", Locale.GERMAN).format(new Date())) + ".txt");
        if (!this.logFile.exists()) {
            try {
                this.logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
