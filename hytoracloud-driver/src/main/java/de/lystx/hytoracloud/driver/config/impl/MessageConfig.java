package de.lystx.hytoracloud.driver.config.impl;



import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class MessageConfig implements Serializable {

    private static final long serialVersionUID = 4275321979476787496L;
    /**
     * The prefix of all messages
     */
    private final String prefix;

    /**
     * When a server is queued
     */
    private final String serviceQueued;

    /**
     * When a server stops
     */
    private final String serviceStop;

    /**
     * When a server is connected
     */
    private final String serviceConnected;

    /**
     * When you're executing "/hub" but
     * you're already on the hub
     */
    private final String alreadyLobby;

    /**
     * When no lobby server could be found
     */
    private final String noLobbyFound;

    /**
     * When the network is in maintenance
     */
    private final String maintenanceNetwork;

    /**
     * When a group is in maintenance
     */
    private final String maintenanceGroup;

    /**
     * When you're already connected to a service
     */
    private final String alreadyConnected;

    /**
     * When a server gets stopped and you're on it
     */
    private final String bukkitShutdown;

    /**
     * When you weren't registered via proxy before
     */
    private final String onlyProxyJoin;

    /**
     * When the cloud is shut down
     * and everyone gets kicked
     */
    private final String cloudShutdown;

}
