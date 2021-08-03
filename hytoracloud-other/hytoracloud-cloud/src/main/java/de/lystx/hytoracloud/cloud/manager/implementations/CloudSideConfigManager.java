package de.lystx.hytoracloud.cloud.manager.implementations;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.config.FileService;
import de.lystx.hytoracloud.driver.config.IConfigManager;
import de.lystx.hytoracloud.driver.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

@Getter @Setter
public class CloudSideConfigManager implements IConfigManager {


    /**
     * The config object
     */
    private NetworkConfig networkConfig;

    /**
     * The json file document
     */
    private JsonDocument jsonDocument;

    public CloudSideConfigManager() {
        this.reload();
    }

    @Override
    public void reload() {
        this.jsonDocument = new JsonDocument(CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getConfigFile());
        if (!CloudDriver.getInstance().getServiceRegistry().getInstance(FileService.class).getConfigFile().exists()) {
            this.jsonDocument.append(NetworkConfig.defaultConfig());
            this.jsonDocument.save();
            return;
        }
        this.networkConfig = jsonDocument.getAs(NetworkConfig.class);
    }

    @Override
    public void shutdown() {
        this.jsonDocument.append(this.networkConfig);
        this.jsonDocument.save();
    }

    @Override
    public JsonDocument getJson() {
        return this.jsonDocument;
    }

    @SneakyThrows
    @Override
    public ProxyConfig getProxyConfig() {
        throw new IllegalAccessException("Only supported on CloudBridge!");
    }

}
