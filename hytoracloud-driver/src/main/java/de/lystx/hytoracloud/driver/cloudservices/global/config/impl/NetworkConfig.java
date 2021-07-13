package de.lystx.hytoracloud.driver.cloudservices.global.config.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketUpdateNetworkConfig;

import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.fallback.Fallback;
import de.lystx.hytoracloud.driver.cloudservices.global.config.impl.fallback.FallbackConfig;

import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter @Setter @AllArgsConstructor
public class NetworkConfig implements Serializable, Objectable<NetworkConfig> {

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
                new LinkedList<>(),
                new MessageConfig(
                        "§8» §bCloud §8┃ §7",
                        "%prefix%§7The server §a%server% §7is now starting§8...",
                        "%prefix%§7The server §c%server% §7is now stopping§8...",
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
                )
        );
    }

    /**
     * Updates this {@link NetworkConfig}
     * to sync it all over the network
     */
    public void update() {
        CloudDriver.getInstance().sendPacket(new PacketUpdateNetworkConfig(this));
    }
}
