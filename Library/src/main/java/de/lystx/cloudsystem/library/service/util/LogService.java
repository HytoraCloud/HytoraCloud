package de.lystx.cloudsystem.library.service.util;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.CloudServiceType;
import de.lystx.cloudsystem.library.service.io.FileService;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter @Setter
public class LogService extends CloudService {

    private final List<String> logs;
    private File logFile;
    private boolean active;

    /**
     * Initialising LogService
     * @param cloudLibrary
     * @param name
     * @param cloudType
     */
    public LogService(CloudLibrary cloudLibrary, String name, CloudServiceType cloudType) {
        super(cloudLibrary, name, cloudType);
        this.logs = new LinkedList<>();
        this.newFile();
        this.active = true;
    }

    /**
     * Logs something
     * @param prefix
     * @param message
     */
    public void log(String prefix, String message) {
        this.log("[" + prefix.toUpperCase() + "] " + message);
    }

    /**
     * Raw logging method
     * @param message
     */
    public void log(String message) {
        if (!this.isActive()) {
            return;
        }
        this.logs.add(message);
    }

    /**
     * Saves current log
     */
    public void save() {
        try {
            for (String log : this.logs) {
                this.getCloudLibrary().getService(FileService.class).write(this.logFile, log);
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
        if (!this.getCloudLibrary().isRunning()) {
            return;
        }
        this.logFile = new File(this.getCloudLibrary().getService(FileService.class).getLogsDirectory(), (new SimpleDateFormat("dd.MM.yyyy_HH_mm_ss", Locale.GERMAN).format(new Date())) + ".txt");
        if (!this.logFile.exists()) {
            try {
                this.logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
