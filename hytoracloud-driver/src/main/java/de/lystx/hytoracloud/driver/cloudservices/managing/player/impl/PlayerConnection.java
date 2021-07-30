package de.lystx.hytoracloud.driver.cloudservices.managing.player.impl;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

/**
 * Class is used
 * to disconnect the player
 * and send packets
 */
@Getter @AllArgsConstructor
public class PlayerConnection implements Serializable {

    private static final long serialVersionUID = -391781264872301460L;
    /**
     * The UUId of this connection
     */
    private final UUID uniqueId;

    /**
     * The name of this connection
     */
    private final String name;

    /**
     * The address (host and port)
     */
    private final String address;

    /**
     * The protocolVersion
     */
    private final int protocolVersion;

    /**
     * If the connection is online (Cracked users)
     */
    private final boolean online;

    /**
     * If its legacy or not
     */
    private final boolean legacyMode;

}
