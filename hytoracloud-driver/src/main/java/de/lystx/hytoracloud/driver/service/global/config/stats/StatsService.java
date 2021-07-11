package de.lystx.hytoracloud.driver.service.global.config.stats;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.service.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.global.main.ICloudService;
import de.lystx.hytoracloud.driver.service.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.other.FileService;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;

@Getter @Setter
@ICloudServiceInfo(
        name = "StatsService",
        type = CloudServiceType.CONFIG,
        description = {
                "This service manages the statistics of the cloud",
                "and updates it and transforms it into a statistics object",
                "And it also saves it into the stats.json - file"
        },
        version = 1.2
)
public class StatsService implements ICloudService {

    private final File file;
    private VsonObject document;

    private Statistics statistics;

    public StatsService() {
        this.file = CloudDriver.getInstance().getInstance(FileService.class).getStatsFile();
        try {
            this.document = new VsonObject(this.file, VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.load();
    }

    /**
     * Loads service
     */
    public void load() {
        this.statistics = new Statistics();
        this.statistics.setFile(this.file);
        this.statistics.load();

        if (this.getDriver().getParent().getWebServer() == null) {
            return;
        }
        this.getDriver().getParent().getWebServer().update("stats", this.statistics.toVson());
    }

    /**
     * Saves service
     */
    public void save() {
        this.statistics.save(this.file);
    }

}
