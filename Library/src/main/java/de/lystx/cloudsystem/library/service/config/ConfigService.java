package de.lystx.cloudsystem.library.service.config;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.file.FileService;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ConfigService extends CloudService {


    private NetworkConfig networkConfig;
    private Document document;

    public ConfigService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.reload();
    }

    public void reload() {

        this.document = Document.fromFile(getCloudLibrary().getService(FileService.class).getConfigFile());
        if (this.document.isEmpty() || !getCloudLibrary().getService(FileService.class).getConfigFile().exists()) {
            this.document.appendAll(NetworkConfig.defaultConfig());
            this.document.save();
        }
        this.networkConfig = document.getObject(document.getJsonObject(), NetworkConfig.class);
    }

    public void save() {
        this.document.appendAll(this.networkConfig);
        this.document.save();
    }
}
