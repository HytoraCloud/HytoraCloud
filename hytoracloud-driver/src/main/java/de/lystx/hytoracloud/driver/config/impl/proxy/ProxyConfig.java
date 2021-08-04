package de.lystx.hytoracloud.driver.config.impl.proxy;



import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.DriverInfo;
import de.lystx.hytoracloud.driver.utils.other.Array;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Getter @Setter @AllArgsConstructor @ToString
public class ProxyConfig implements Serializable {

    /**
     * If enabled
     */
    private boolean enabled;

    /**
     * If cracked users
     */
    private boolean onlineMode;

    /**
     * Tablist update delay
     */
    private long tabListDelay;

    /**
     * all tablists
     */
    private List<TabList> tabList;

    /**
     * All motds for normal
     */
    private List<Motd> motdNormal;

    /**
     * All motds for maintenance
     */
    private List<Motd> motdMaintenance;


    /**
     * Creates default ProxyConfig
     * @return ProxyConfig
     */
    public static ProxyConfig defaultConfig() {
        DriverInfo driverInfo = CloudDriver.class.getAnnotation(DriverInfo.class);
        return new ProxyConfig(
                true,
                true,
                     20L,
                     Collections.singletonList(
                        new TabList(
                                true,
                                new String[]
                                        {
                                            "&8",
                                            "&8        &8» &bHytoraCloud &8«        &8",
                                            "&8        &3Server &8» &7%server%      &8",
                                            "&8        &3Proxy &8» &7%proxy%        &8",
                                            "&8"
                                        },
                                new String[]
                                        {
                                            "&8",
                                            "&8 &3Twitter &8» &7@HytoraCloud &8┃ &3Developer &8» &7Lystx &8",
                                            "&8"
                                        }
                        )),
                         Collections.singletonList(
                                 new Motd(
                                    true,
                                    "&8» &bHytoraCloud &8&l‴&7&l‴ &7your &bcloudSystem &8[&f" + driverInfo.lowestSupportVersion() + "&7-&f" + driverInfo.highestSupportVersion() + "&8]",
                                    "&8» &3Status &8× §aOnline §8┃ §7Proxy &8× §3%proxy%",
                                    new String[0],
                                    ""
                                )
                         ),
                Collections.singletonList(
                        new Motd(
                                true,
                                "&8» &bHytoraCloud &8&l‴&7&l‴ &7your &bcloudSystem &8[&f" + driverInfo.lowestSupportVersion() + "&7-&f" + driverInfo.highestSupportVersion() + "&8]",
                                "&8» &3Status &8× §cMaintenance §8┃ §7Proxy &8× §3%proxy%",
                                new String[]{
                                        "§bHytoraCloud §7Information",
                                        "§8§m--------------------------",
                                        "§8",
                                        "&bVersion &8» &7" + driverInfo.version(),
                                        "&bThanks to &8» &7" + new Array<>(driverInfo.contributors()),
                                        "&bTwitter &8» &7@HytoraCloud",
                                        "&bDiscord &8» &7pazzqaGSVs",
                                        "§8",
                                        "§8§m--------------------------",
                                        "§8"
                                },
                                "&8» &c&oMaintenance"
                        )
                )
        );
    }

}
