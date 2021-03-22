package de.lystx.cloudsystem.library.service.config;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.enums.CloudType;
import de.lystx.cloudsystem.library.elements.other.ReceiverInfo;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.config.impl.NetworkConfig;
import de.lystx.cloudsystem.library.service.io.FileService;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class ConfigService extends CloudService {


    private NetworkConfig networkConfig;
    private ReceiverInfo receiverInfo;
    private VsonObject vsonObject;
    int tries = 0;

    public ConfigService(CloudLibrary cloudLibrary, String name, CloudServiceType type) {
        super(cloudLibrary, name, type);
        this.reload();
    }

    /**
     * (Re-)loads config
     */
    public void reload() {
        try {
            tries += 1;
            this.vsonObject = new VsonObject(getCloudLibrary().getService(FileService.class).getConfigFile(), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            if (!getCloudLibrary().getService(FileService.class).getConfigFile().exists()) {
                if (this.getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
                    this.vsonObject.putAll(NetworkConfig.defaultConfig());
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("proxyStartPort", 25565);
                    map.put("serverStartPort", 30000);
                    this.vsonObject.putAll(new ReceiverInfo("Receiver-1", "127.0.0.1", 0, false, map));
                }
                this.vsonObject.save();
                if (tries <= 10) {
                    this.reload();
                } else {
                    System.out.println("[Config] Tried 10 times to reload!");
                }
                return;
            }
            if (this.getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
                this.receiverInfo = null;
                if (vsonObject.has("useWrapper")) {
                    vsonObject.remove("useWrapper");
                    vsonObject.save();
                }
               this.networkConfig = vsonObject.getAs(NetworkConfig.class);
            } else {
                this.receiverInfo = vsonObject.getAs(ReceiverInfo.class);
                this.networkConfig = null;
                this.getCloudLibrary().getCustoms().put("receiverInfo", this.receiverInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.vsonObject.getVsonSettings().add(VsonSettings.CREATE_FILE_IF_NOT_EXIST);
        this.vsonObject.getVsonSettings().add(VsonSettings.OVERRITE_VALUES);
    }

    /**
     * saves config
     */
    public void save() {
        if (this.getCloudLibrary().getCloudType().equals(CloudType.CLOUDSYSTEM)) {
            this.vsonObject.putAll(this.networkConfig);
        } else {
            this.vsonObject.putAll(this.receiverInfo);
        }
        this.vsonObject.getVsonSettings().add(VsonSettings.CREATE_FILE_IF_NOT_EXIST);
        this.vsonObject.getVsonSettings().add(VsonSettings.OVERRITE_VALUES);
        this.vsonObject.save();
    }
}
