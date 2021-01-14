package de.lystx.cloudsystem.library.service.config.impl.proxy;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
public class ProxyConfig implements Serializable {

    private boolean enabled;
    private boolean maintenance;
    private int maxPlayers;
    private List<String> whitelistedPlayers;
    private TabList tabList;
    private Motd motdNormal;
    private Motd motdMaintenance;

    public ProxyConfig(boolean enabled, boolean maintenance, int maxPlayers, List<String> whitelistedPlayers, TabList tabList, Motd motdNormal, Motd motdMaintenance) {
        this.enabled = enabled;
        this.maintenance = maintenance;
        this.maxPlayers = maxPlayers;
        this.whitelistedPlayers = whitelistedPlayers;
        this.tabList = tabList;
        this.motdNormal = motdNormal;
        this.motdMaintenance = motdMaintenance;
    }
}
