package de.lystx.hytoracloud.driver.cloudservices.global.config;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import de.lystx.hytoracloud.driver.cloudservices.global.main.CloudServiceType;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudService;
import de.lystx.hytoracloud.driver.cloudservices.global.main.ICloudServiceInfo;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.NetworkConfig;
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
    private JsonDocument jsonDocument;
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
        this.jsonDocument = new JsonDocument(getDriver().getInstance(FileService.class).getConfigFile());
        if (!getDriver().getInstance(FileService.class).getConfigFile().exists()) {
            this.jsonDocument.append(NetworkConfig.defaultConfig());
            this.jsonDocument.save();
            if (tries <= 10) {
                this.reload();
            } else {
                System.out.println("[Config] Tried 10 times to reload!");
            }
            return;
        }
        this.networkConfig = jsonDocument.getAs(NetworkConfig.class);
        CloudDriver.getInstance().setNetworkConfig(this.networkConfig);
    }

    public void shutdown() {
        this.jsonDocument.append(this.networkConfig);
        this.jsonDocument.save();

        CloudDriver.getInstance().setNetworkConfig(this.networkConfig);
    }

    /**
     * saves config
     */
    public void save() {
    }
}
