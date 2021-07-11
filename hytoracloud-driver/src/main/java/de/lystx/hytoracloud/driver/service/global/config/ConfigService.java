package de.lystx.hytoracloud.driver.service.global.config;

import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.utils.utillity.ReceiverInfo;
import de.lystx.hytoracloud.driver.service.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.service.global.main.ICloudService;
import de.lystx.hytoracloud.driver.service.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.service.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.service.other.FileService;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
@ICloudServiceInfo(
        name = "ConfigService",
        type = CloudServiceType.CONFIG,
        description = {
                "This service manages and stores the config of the cloud"
        },
        version = 1.3
)
public class ConfigService implements ICloudService {


    private NetworkConfig networkConfig;
    private ReceiverInfo receiverInfo;
    private VsonObject vsonObject;
    int tries = 0;

    public ConfigService() {
        this.reload();
    }

    /**
     * (Re-)loads config
     */
    public void reload() {
        try {
            tries += 1;
            this.vsonObject = new VsonObject(getDriver().getInstance(FileService.class).getConfigFile(), VsonSettings.CREATE_FILE_IF_NOT_EXIST, VsonSettings.OVERRITE_VALUES);
            if (!getDriver().getInstance(FileService.class).getConfigFile().exists()) {
                if (this.getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
                    this.vsonObject.clone(new VsonObject().append(NetworkConfig.defaultConfig()));
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
            if (this.getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
                this.receiverInfo = null;
                if (vsonObject.has("useWrapper")) {
                    vsonObject.remove("useWrapper");
                    vsonObject.save();
                }
               this.networkConfig = vsonObject.getAs(NetworkConfig.class);
            } else {
                this.receiverInfo = vsonObject.getAs(ReceiverInfo.class);
                this.networkConfig = null;
                this.getDriver().getImplementedData().put("receiverInfo", this.receiverInfo);
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
        if (this.getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            this.vsonObject.putAll(this.networkConfig);
        } else {
            this.vsonObject.putAll(this.receiverInfo);
        }
        this.vsonObject.getVsonSettings().add(VsonSettings.CREATE_FILE_IF_NOT_EXIST);
        this.vsonObject.getVsonSettings().add(VsonSettings.OVERRITE_VALUES);
        this.vsonObject.save();
    }
}
