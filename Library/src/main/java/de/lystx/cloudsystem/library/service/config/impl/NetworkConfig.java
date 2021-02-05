package de.lystx.cloudsystem.library.service.config.impl;

import de.lystx.cloudsystem.library.service.config.impl.fallback.Fallback;
import de.lystx.cloudsystem.library.service.config.impl.fallback.FallbackConfig;
import de.lystx.cloudsystem.library.service.config.impl.labymod.LabyModConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.Motd;
import de.lystx.cloudsystem.library.service.config.impl.proxy.ProxyConfig;
import de.lystx.cloudsystem.library.service.config.impl.proxy.TabList;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

@Getter @Setter
public class NetworkConfig implements Serializable {

    private String host;
    private Integer port;
    private boolean useWrapper;
    private boolean setupDone;
    private boolean autoUpdater;
    private boolean proxyProtocol;

    private ProxyConfig proxyConfig;
    private LabyModConfig labyModConfig;
    private MessageConfig messageConfig;
    private FallbackConfig fallbackConfig;

    public NetworkConfig(String host, Integer port, boolean useWrapper, boolean setupDone, boolean autoUpdater, boolean proxyProtocol, ProxyConfig proxyConfig, LabyModConfig labyModConfig, MessageConfig messageConfig, FallbackConfig fallbackConfig) {
        this.host = host;
        this.port = port;
        this.useWrapper = useWrapper;
        this.setupDone = setupDone;
        this.autoUpdater = autoUpdater;
        this.proxyProtocol = proxyProtocol;
        this.proxyConfig = proxyConfig;
        this.labyModConfig = labyModConfig;
        this.messageConfig = messageConfig;
        this.fallbackConfig = fallbackConfig;
    }


    public static NetworkConfig defaultConfig() {
        return new NetworkConfig(
                "127.0.0.1",
                2131,
                false,
                false,
                true,
                true,
                new ProxyConfig(
                        true,
                        true,
                        true,
                        100,
                        new LinkedList<>(),
                        new TabList(
                                true,
                                "&8┃&8&m------------------------------------------&8┃&r\n\n&8» &bHytoraCloud &8&l‴&7&l‴ &7your &bcloudSystem&7! &8«\n&7Service &8&l‴&7&l‴ &b%server% &8┃ &7Online &8&l‴&7&l‴ &b%online_players%&8/&b%max_players%\n",
                                "\n&8» &7Developer &8┃ &bLystx &8«\n &8» &7Proxy &8┃ &b%proxy%&8«\n\n&8┃&8&m------------------------------------------&8┃"
                        ),
                        new Motd(
                                true,
                                "&r  &bHytoraCloud &8&l‴&7&l‴ &7your &bcloudSystem&8! &8[&f1.8 &8- &b1.16&8]",
                                "&r  &8» &7News &8× &7Now Public &8» §a%proxy%",
                                "&8&m--------------------------------------------||&7||&7||&7                 &8» &bCloudSystem &8┃ &7made for &byou&8                 &8||    &8||&7                 &8➜ &bTwitter &8● &7@HytoraCloud         &8||&7                 &8➜ &bDiscord &8● &7discord.io/HytoraCloud    &8||&7                 &8➜ &bSpigotSupport &8● &71.8 &8- &71.16.1   &8||&8||&8||&8&m-------------------------------------------",
                                ""
                        ),
                        new Motd(
                                true,
                                "&r  &bHytoraCloud &8&l‴&7&l‴ &7your &bcloudSystem&8! &8[&f1.8 &8- &b1.16&8]",
                                "&r  &8» &cWe are in §emaintenance &8» §a%proxy%",
                                "&8&m--------------------------------------------||&7||&7||&7                 &8» &bCloudSystem &8┃ &7made for &byou&8                 &8||    &8||&7                 &8➜ &bTwitter &8● &7@HytoraCloud         &8||&7                 &8➜ &bDiscord &8● &7discord.io/HytoraCloud    &8||&7                 &8➜ &bSpigotSupport &8● &71.8 &8- &71.16.1   &8||&8||&8||&8&m-------------------------------------------",
                                "&8» &c&oMaintenance"
                        )
                ),
                new LabyModConfig(
                        false,
                        "§8» §7HytoraCloud §8× §b%service%",
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

}
