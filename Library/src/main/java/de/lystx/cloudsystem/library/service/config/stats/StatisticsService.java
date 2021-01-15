package de.lystx.cloudsystem.library.service.config.stats;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.file.FileService;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter @Setter
public class StatisticsService extends CloudService {

    private final File file;
    private final Document document;

    private Statistics statistics;

    public StatisticsService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.file = cloudLibrary.getService(FileService.class).getStatsFile();
        this.document = Document.fromFile(this.file);
        this.load();
    }

    public void load() {
        this.statistics = new Statistics();
        this.statistics.setFile(this.file);
        this.statistics.load();
    }

    public void save() {
        this.statistics.save();
    }

}
