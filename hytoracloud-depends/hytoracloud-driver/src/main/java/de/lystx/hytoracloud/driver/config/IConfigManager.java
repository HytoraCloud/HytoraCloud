package de.lystx.hytoracloud.driver.config;

import de.lystx.hytoracloud.driver.config.impl.NetworkConfig;
import de.lystx.hytoracloud.driver.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.utils.json.JsonDocument;

public interface IConfigManager {

    /**
     * Gets the {@link NetworkConfig} for the
     * global HytoraCloud-System
     *
     * @return config
     */
    NetworkConfig getNetworkConfig();

    /**
     * Sets the {@link NetworkConfig}
     *
     * @param networkConfig the config
     */
    void setNetworkConfig(NetworkConfig networkConfig);

    /**
     * Tries to get the {@link ProxyConfig}
     * for the current {@link de.lystx.hytoracloud.driver.service.IService} instance
     * if its proxy and a {@link ProxyConfig} is set for the group of it
     *
     * @return config or null
     */
    ProxyConfig getProxyConfig();

    /**
     * Shuts down this manager
     */
    void shutdown();

    /**
     * Reloads this manager
     */
    void reload();

    /**
     * The json data where the config is stored
     *
     * @return json or null if bridge
     */
    JsonDocument getJson();
}
