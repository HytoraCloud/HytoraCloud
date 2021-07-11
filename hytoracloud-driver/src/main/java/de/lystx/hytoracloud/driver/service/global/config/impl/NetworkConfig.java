package de.lystx.hytoracloud.driver.service.global.config.impl;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketUpdateNetworkConfig;

import de.lystx.hytoracloud.driver.service.global.config.impl.fallback.Fallback;
import de.lystx.hytoracloud.driver.service.global.config.impl.fallback.FallbackConfig;
import de.lystx.hytoracloud.driver.service.global.config.impl.labymod.LabyModConfig;
import de.lystx.hytoracloud.driver.service.global.config.impl.proxy.GlobalProxyConfig;

import io.vson.elements.object.Objectable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

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
     * If auto updater is enabled
     */
    private boolean autoUpdater;

    /**
     * The GlobalProxyConfig
     */
    private GlobalProxyConfig globalProxyConfig;

    /**
     * The LabyMod config
     */
    private LabyModConfig labyModConfig;

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
                false,
                new GlobalProxyConfig(
                        25565,
                        30000,
                        false,
                        true,
                        true,
                        new LinkedList<>()
                ),
                new LabyModConfig(
                        false,
                        "§8» §7HytoraCloud §8× §b%service% §8[§b%online_players%§8/§b%max_players%§8]",
                        true
                ),
                new MessageConfig(
                        "§8» §bCloud §8┃ §7",
                        "%prefix%§7The server §a%server% §7is now starting§8...",
                        "%prefix%§7The server §c%server% §7is now stopping§8...",
                        "%prefix%§cYou are already on a lobbyserver!",
                        "%prefix%§cNo lobbyserver could be found!",
                        "%prefix%§cThe network is not available for you at this time",
                        "%prefix%§cThe CloudSystem is still booting up! There are no servers to connect on at this time!",
                        "%prefix%§cThe servergroup §e%group% §cis in maintenance!",
                        "%prefix%§cYou are alread connected to this service!",
                        "%prefix%§cYou are alread on the network!",
                        "%prefix%§cThis server was shut down!",
                        "%prefix%§cAn error occured§8: §e%error%"
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
