package de.lystx.cloudsystem.library.service.backup;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.file.FileService;
import de.lystx.cloudsystem.library.service.scheduler.Scheduler;
import de.lystx.cloudsystem.library.service.util.Value;
import de.lystx.cloudsystem.library.service.util.ZipHelper;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class BackupService extends CloudService {

    private final Document document;
    private TimeUnit timeUnit;
    private int interval;
    private long lastBackup;
    private boolean enabled;

    public BackupService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.document = new Document(cloudLibrary.getService(FileService.class).getBackupFile());
        this.load();
        this.start();
    }

    public void load() {
        this.timeUnit = TimeUnit.valueOf(this.document.getString("timeUnit", "DAYS"));
        this.interval = this.document.getInteger("interval", 1);
        this.enabled = this.document.getBoolean("enabled", true);
        this.lastBackup = this.document.getLong("lastBackup", new Date().getTime());
        this.document.save();
    }

    public void start() {
        if (!this.enabled) {
            return;
        }
        long l = this.timeUnit.toMillis(this.interval);
        if (this.timeUnit.equals(TimeUnit.SECONDS)) {
            l = (20L * interval);
        } else if (this.timeUnit.equals(TimeUnit.MINUTES)) {
            l = ((60*20L) * interval);
        } else if (this.timeUnit.equals(TimeUnit.HOURS)) {
            l = ((60*(60*20L)) * interval);
        } else if (this.timeUnit.equals(TimeUnit.DAYS)) {
            l = ((24*(60*(60*20L))) * interval);
        }
        Value<Integer> i = new Value<>(-1);
        this.getCloudLibrary().getService(Scheduler.class).scheduleRepeatingTask(() -> {
            int finalInt = i.get() + 1;
            i.set(finalInt);
            if (finalInt != 0) {
                this.createBackup(UUID.randomUUID().toString());
            }
        }, 0, l);
    }

    public void createBackup(String name) {
        this.getCloudLibrary().getConsole().getLogger().sendMessage("INFO", "§7Now creating new Backup with UUID §b" + name + "§7!");
        this.getCloudLibrary().getConsole().getLogger().sendMessage("INFO", "§aNext Backup will be created in §a" + this.interval + " " + this.timeUnit.name());
        this.lastBackup = new Date().getTime();

        File src = this.getCloudLibrary().getService(FileService.class).getCloudDirectory();
        ZipHelper zipHelper = new ZipHelper(new File(src.toString() + ".zip"),  new File(getCloudLibrary().getService(FileService.class).getBackupDirectory(), name + "/"));
        zipHelper.zip();

    }
}
