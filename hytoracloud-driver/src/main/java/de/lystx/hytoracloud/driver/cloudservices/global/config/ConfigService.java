package de.lystx.hytoracloud.driver.cloudservices.global.config;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import utillity.JsonEntity;
import utillity.ReceiverInfo;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import lombok.Getter;
import lombok.Setter;

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
    private JsonEntity jsonEntity;
    int tries = 0;

    public void setNetworkConfig(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;
        CloudDriver.getInstance().setNetworkConfig(networkConfig);
    }

    public ConfigService() {
        this.reload();
    }

    /**
     * (Re-)loads config
     */
    public void reload() {
        tries += 1;
        this.jsonEntity = new JsonEntity(getDriver().getInstance(FileService.class).getConfigFile());
        if (!getDriver().getInstance(FileService.class).getConfigFile().exists()) {
            if (this.getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
                this.jsonEntity.append(NetworkConfig.defaultConfig());
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("proxyStartPort", 25565);
                map.put("serverStartPort", 30000);
                this.jsonEntity.append(new ReceiverInfo("Receiver-1", "127.0.0.1", 0, false, map));
            }
            this.jsonEntity.save();
            if (tries <= 10) {
                this.reload();
            } else {
                System.out.println("[Config] Tried 10 times to reload!");
            }
            return;
        }
        if (this.getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            this.receiverInfo = null;
            this.networkConfig = jsonEntity.getAs(NetworkConfig.class);
        } else {
            this.receiverInfo = jsonEntity.getAs(ReceiverInfo.class);
            this.networkConfig = null;
            this.getDriver().getImplementedData().put("receiverInfo", this.receiverInfo);
        }
        CloudDriver.getInstance().setNetworkConfig(this.networkConfig);
    }

    public void shutdown() {
        if (this.getDriver().getDriverType().equals(CloudType.CLOUDSYSTEM)) {
            this.jsonEntity.append(this.networkConfig);
        } else {
            this.jsonEntity.append(this.receiverInfo);
        }
        this.jsonEntity.save();

        CloudDriver.getInstance().setNetworkConfig(this.networkConfig);
    }

    /**
     * saves config
     */
    public void save() {
    }
}
