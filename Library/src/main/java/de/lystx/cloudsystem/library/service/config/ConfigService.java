package de.lystx.cloudsystem.library.service.config;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.other.Document;
import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.file.FileService;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ConfigService extends CloudService {


    private NetworkConfig networkConfig;
    private ReceiverInfo receiverInfo;
    private Document document;

    public ConfigService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.reload();
    }

    public void reload() {
        this.document = Document.fromFile(getCloudLibrary().getService(FileService.class).getConfigFile());
        if (!getCloudLibrary().getService(FileService.class).getConfigFile().exists()) {
            if (this.getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
                this.document.append(NetworkConfig.defaultConfig());
            } else {
                this.document.append(new ReceiverInfo("Receiver-1", "127.0.0.1", 0, false));
            }
            this.document.save();
            this.reload();
            return;
        }
        if (this.getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
            this.receiverInfo = null;
            this.networkConfig = document.getAs(NetworkConfig.class);
        } else {
            this.receiverInfo = document.getAs(ReceiverInfo.class);
            this.networkConfig = null;
        }
    }

    public void save() {
        if (this.getCloudLibrary().getType().equals(CloudLibrary.Type.CLOUDSYSTEM)) {
            this.document.append(this.networkConfig);
        } else {
            this.document.append(this.receiverInfo);
        }
        this.document.save();
    }
}
