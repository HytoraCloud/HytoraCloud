package de.lystx.cloudsystem.library.service.util;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.file.FileService;
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

    public LogService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.logs = new LinkedList<>();
        this.newFile();
        this.active = true;
    }

    public void log(String prefix, String message) {
        this.log("[" + prefix.toUpperCase() + "] " + message);
    }

    public void log(String message) {
        if (!this.isActive()) {
            return;
        }
        this.logs.add(message);
    }

    public void save() {
        try {
            for (String log : this.logs) {
                this.getCloudLibrary().getService(FileService.class).write(this.logFile, log);
            }
            this.newFile();
        } catch (NullPointerException | ConcurrentModificationException ignored) {}
    }

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
