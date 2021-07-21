package de.lystx.hytoracloud.driver.cloudservices.global.config;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.utillity.JsonEntity;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.cloudservices.other.FileService;
import lombok.Getter;
import lombok.Setter;

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
            this.jsonEntity.append(NetworkConfig.defaultConfig());
            this.jsonEntity.save();
            if (tries <= 10) {
                this.reload();
            } else {
                System.out.println("[Config] Tried 10 times to reload!");
            }
            return;
        }
        this.networkConfig = jsonEntity.getAs(NetworkConfig.class);
        CloudDriver.getInstance().setNetworkConfig(this.networkConfig);
    }

    public void shutdown() {
        this.jsonEntity.append(this.networkConfig);
        this.jsonEntity.save();

        CloudDriver.getInstance().setNetworkConfig(this.networkConfig);
    }

    /**
     * saves config
     */
    public void save() {
    }
}
