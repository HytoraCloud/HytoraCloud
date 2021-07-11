package de.lystx.hytoracloud.driver.service.global.config.impl.proxy;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Getter @Setter @AllArgsConstructor @ToString
public class ProxyConfig implements Serializable {

    private boolean enabled;
    private boolean onlineMode;
    private int maxPlayers;
    private long tabListDelay;
    private List<TabList> tabList;
    private List<Motd> motdNormal;
    private List<Motd> motdMaintenance;


    /**
     * Creates default ProxyConfig
     * @return ProxyConfig
     */
    public static ProxyConfig defaultConfig() {
        return new ProxyConfig(
                true,
                true,
                100,
                     20L,
                     Collections.singletonList(
                        new TabList(
                                true,
                                "&8┃&8&m------------------------------------------&8┃&r\n\n&8» &bHytoraCloud &8&l‴&7&l‴ &7your &bcloudSystem&7! &8«\n&7Service &8&l‴&7&l‴ &b%server% &8┃ &7Online &8&l‴&7&l‴ &b%online_players%&8/&b%max_players%\n",
                                "\n&8» &7Developer &8┃ &bLystx &8«\n &8» &7Proxy &8┃ &b%proxy%&8«\n\n&8┃&8&m------------------------------------------&8┃"
                        )),
                         Collections.singletonList(
                                 new Motd(
                                    true,
                                    "&r  &bHytoraCloud &8&l‴&7&l‴ &7your &bcloudSystem&8! &8[&f1.8 &8- &b1.16&8]",
                                    "&r  &8» &7News &8× &7Now Public &8» §a%proxy%",
                                    "",
                                    ""
                                )
                         ),
                Collections.singletonList(
                        new Motd(
                                true,
                                "&r  &bHytoraCloud &8&l‴&7&l‴ &7your &bcloudSystem&8! &8[&f1.8 &8- &b1.16&8]",
                                "&r  &8» &cWe are in §emaintenance &8» §a%proxy%",
                                "&8&m--------------------------------------------||&7||&7||&7                 &8» &bCloudSystem &8┃ &7made for &byou&8                 &8||    &8||&7                 &8➜ &bTwitter &8● &7@HytoraCloud         &8||&7                 &8➜ &bDiscord &8● &7discord.io/HytoraCloud    &8||&7                 &8➜ &bSpigotSupport &8● &71.8 &8- &71.16.1   &8||&8||&8||&8&m-------------------------------------------",
                                "&8» &c&oMaintenance"
                        )
                )
        );
    }

}
