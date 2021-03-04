package de.lystx.cloudsystem.library.service.config.impl.proxy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter @AllArgsConstructor
public class ProxyConfig implements Serializable {

    private boolean enabled;
    private boolean maintenance;
    private boolean hubCommandEnabled;
    private int maxPlayers;
    private List<String> whitelistedPlayers;
    private TabList tabList;
    private Motd motdNormal;
    private Motd motdMaintenance;

}
