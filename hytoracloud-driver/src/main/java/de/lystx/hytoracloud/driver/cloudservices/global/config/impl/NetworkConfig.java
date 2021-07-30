package de.lystx.hytoracloud.driver.cloudservices.global.config.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.global.config.ConfigService;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.proxy.ProxyConfig;
import de.lystx.hytoracloud.driver.commons.enums.cloud.CloudType;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketUpdateNetworkConfig;

import de.lystx.hytoracloud.driver.cloudservices.managing.fallback.Fallback;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.fallback.FallbackConfig;

import de.lystx.hytoracloud.driver.commons.storage.CloudMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

@Getter @Setter @AllArgsConstructor
public class NetworkConfig implements Serializable {

    private static final long serialVersionUID = 2412230558827763090L;
    /**
     * The host of the cloud
     */
    private String host;

    /**
     * The port of the cloud
     */
    private Integer port;

    /**
     * If the cloud is set up
     */
    private boolean setupDone;

    /**
     * The proxy start port
     */
    private Integer proxyStartPort;

    /**
     * The server start port
     */
    private Integer serverStartPort;

    /**
     * If proxy protocol is enabled
     */
    private boolean proxyProtocol;

    /**
     * If the network is in maintenance
     */
    private boolean maintenance;

    /**
     * Maxmimum players on the network
     */
    private int maxPlayers;

    /**
     * The whitelisted players
     */
    private List<String> whitelistedPlayers;

    /**
     * The message config
     */
    private MessageConfig messageConfig;

    /**
     * The fallback config
     */
    private FallbackConfig fallbackConfig;

    /**
     * The proxy configs for different groups
     */
    private Map<String, ProxyConfig> proxyConfigs;

    /**
     * Create default Config for everything
     *
     * @return default config
     */
    public static NetworkConfig defaultConfig() {
        return new NetworkConfig("localhost",
                1401,
                false,
                25565,
                30000,
                false,
                true,
                100,
                new LinkedList<>(),
                new MessageConfig(
                        "§8» §bCloud §8┃ §7",
                        "%prefix%§7The server §e%server% §7is now queued§8...",
                        "%prefix%§7The server §c%server% §7is now stopping§8...",
                        "%prefix%§7The server §a%server% §7is now connected§8!",
                        "%prefix%§cYou are already on a Lobby-Server!",
                        "%prefix%§cNo Lobby-Server could be found!",
                        "%prefix%§cThe network is not available for you at this time",
                        "%prefix%§cThe group §e%group% §cis in maintenance!",
                        "%prefix%§cYou are already connected to this Service!",
                        "%prefix%§cThis server was shut down!",
                        "%prefix%§cPlease only join through any of the Proxys!",
                        "%prefix%§cHytoraCloud was §estopped§c!"
                        ),
                new FallbackConfig(
                        new Fallback(1, "Lobby", null),
                        new ArrayList<>()
                ),
                new CloudMap<String, ProxyConfig>().append("Bungee", ProxyConfig.defaultConfig())
        );
    }

    /**
     * Updates this {@link NetworkConfig}
     * to sync it all over the network
     */
    public void update() {
        if (CloudDriver.getInstance().getDriverType() == CloudType.CLOUDSYSTEM) {
            ConfigService instance = CloudDriver.getInstance().getInstance(ConfigService.class);
            instance.setNetworkConfig(this);
            instance.shutdown();
            instance.reload();
        }
        CloudDriver.getInstance().sendPacket(new PacketUpdateNetworkConfig(this));
    }
}
