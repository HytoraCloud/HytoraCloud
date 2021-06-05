package de.lystx.hytoracloud.driver.service.other;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.main.ICloudService;
import de.lystx.hytoracloud.driver.service.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.scheduler.Scheduler;
import de.lystx.hytoracloud.driver.service.util.utillity.Value;
import de.lystx.hytoracloud.driver.service.util.other.Zip;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@ICloudServiceInfo(
        name = "BackupService",
        type = CloudServiceType.MANAGING,
        description = {
                "This class is used to manage all backups of HytoraCloud"
        },
        version = 1.0
)
public class BackupService implements ICloudService {

    private final VsonObject document;
    private TimeUnit timeUnit;
    private int interval;
    private long lastBackup;
    private boolean enabled;

    public BackupService() {
        VsonObject finalDocument = null;
        try {
            finalDocument = new VsonObject(CloudDriver.getInstance().getInstance(FileService.class).getBackupFile(), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.document = finalDocument;
        this.load();
        this.start();
    }

    /**
     * Loads config of backupservice
     */
    public void load() {
        this.timeUnit = TimeUnit.valueOf(this.document.getString("timeUnit", "DAYS"));
        this.interval = this.document.getInteger("interval", 1);
        this.enabled = this.document.getBoolean("enabled", false);
        this.lastBackup = this.document.getLong("lastBackup", new Date().getTime());
        this.document.save();
    }

    /**
     * Starts couting
     */
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
        this.getDriver().getInstance(Scheduler.class).scheduleRepeatingTask(() -> {
            int finalInt = i.get() + 1;
            i.setValue(finalInt);
            if (finalInt != 0) {
                this.createBackup(UUID.randomUUID().toString());
            }
        }, 0, l);
    }

    /**
     * Creating backup with given name
     * @param name
     */
    public void createBackup(String name) {
        this.getDriver().getParent().getConsole().getLogger().sendMessage("INFO", "§7Now creating new Backup with UUID §b" + name + "§7!");
        this.getDriver().getParent().getConsole().getLogger().sendMessage("INFO", "§aNext Backup will be created in §a" + this.interval + " " + this.timeUnit.name());
        this.lastBackup = new Date().getTime();

        File src = this.getDriver().getInstance(FileService.class).getCloudDirectory();
        Zip zip = new Zip();
        zip.zip(src, new File(getDriver().getInstance(FileService.class).getBackupDirectory(), name + ".zip"));

    }
}
