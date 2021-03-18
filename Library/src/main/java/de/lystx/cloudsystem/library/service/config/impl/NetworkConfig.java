package de.lystx.cloudsystem.library.service.config.impl;

import de.lystx.cloudsystem.library.service.config.impl.fallback.Fallback;
import de.lystx.cloudsystem.library.service.config.impl.fallback.FallbackConfig;
import de.lystx.cloudsystem.library.service.config.impl.labymod.LabyModConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.GlobalProxyConfig;
import io.vson.elements.object.Objectable;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

@Getter @Setter @AllArgsConstructor
public class NetworkConfig implements Serializable, Objectable<NetworkConfig> {

    private String host;
    private Integer port;
    private boolean useWrapper;
    private boolean setupDone;
    private boolean autoUpdater;

    private GlobalProxyConfig networkConfig;
    private LabyModConfig labyModConfig;
    private MessageConfig messageConfig;
    private FallbackConfig fallbackConfig;


    /**
     * Create default Config for everything
     * @return VsonObject
     */
    public static VsonObject defaultConfig() {
        return new VsonObject(VsonSettings.CREATE_FILE_IF_NOT_EXIST)
                .append("host", 0)
                .append("port", 1401)
                .append("useWrapper", false)
                .append("setupDone", false)
                .append("autoUpdater", false)
                .append("networkConfig", new GlobalProxyConfig(
                        25565,
                        30000,
                        false,
                        true,
                        true,
                        new LinkedList<>()
                ))
                .append("labyModConfig",
                    new LabyModConfig(
                            false,
                            "§8» §7HytoraCloud §8× §b%service% §8[§b%online_players%§8/§b%max_player%§8]",
                            true
                    ))
                .append("messageConfig",
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
                            ))
                .append("fallbackConfig",
                    new FallbackConfig(
                            new Fallback(1, "Lobby", null),
                            new ArrayList<>()
                    )
        );
    }

}
