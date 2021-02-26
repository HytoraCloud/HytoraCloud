package de.lystx.cloudsystem.library.service.config.stats;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.file.FileService;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;

@Getter @Setter
public class StatisticsService extends CloudService {

    private final File file;
    private VsonObject document;

    private Statistics statistics;

    public StatisticsService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.file = cloudLibrary.getService(FileService.class).getStatsFile();
        try {
            this.document = new VsonObject(this.file, VsonSettings.OVERRITE_VALUES, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.load();
    }

    public void load() {
        this.statistics = new Statistics();
        this.statistics.setFile(this.file);
        this.statistics.load();

        if (this.getCloudLibrary().getWebServer() == null) {
            return;
        }
        this.getCloudLibrary().getWebServer().update("stats", this.statistics.toVson());
    }

    public void save() {
        this.statistics.save(this.file);
    }

}
