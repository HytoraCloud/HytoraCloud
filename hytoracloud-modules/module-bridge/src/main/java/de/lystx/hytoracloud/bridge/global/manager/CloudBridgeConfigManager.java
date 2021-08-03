package de.lystx.hytoracloud.bridge.global.manager;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.config.IConfigManager;
import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;
import de.lystx.hytoracloud.driver.utils.json.JsonObject;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CloudBridgeConfigManager implements IConfigManager {

    private NetworkConfig networkConfig;


    public void setNetworkConfig(NetworkConfig networkConfig) {
        this.networkConfig = networkConfig;

        try {
            CloudDriver.getInstance().getPortService().setServerPort(networkConfig.getServerStartPort());
            CloudDriver.getInstance().getPortService().setProxyPort(networkConfig.getProxyStartPort());
        } catch (NullPointerException e) {
            //Ignoring
        }
    }

    @Override
    public ProxyConfig getProxyConfig() {
        NetworkConfig networkConfig = CloudDriver.getInstance().getConfigManager().getNetworkConfig();
        if (networkConfig == null || CloudDriver.getInstance().getServiceManager().getThisService() == null) {
            return ProxyConfig.defaultConfig();
        }
        ProxyConfig proxyConfig = networkConfig.getProxyConfigs().get(CloudDriver.getInstance().getServiceManager().getThisService().getGroup().getName());
        return proxyConfig == null ? ProxyConfig.defaultConfig() : proxyConfig;
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void reload() {
    }

    @Override
    public JsonDocument getJson() {
        return (JsonDocument) JsonObject.gson();
    }
}
