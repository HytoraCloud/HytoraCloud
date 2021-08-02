package de.lystx.hytoracloud.driver.cloudservices.managing.player.required;

import de.lystx.hytoracloud.driver.commons.enums.versions.MinecraftProtocol;
import de.lystx.hytoracloud.driver.commons.interfaces.Identifiable;

import java.net.InetSocketAddress;


public interface IPlayerConnection extends Identifiable {

    /**
     * The host of this connection
     *
     * @return host as {@link String}
     */
    String getHost();

    /**
     * The port of this connection
     *
     * @return port as {@link Integer}
     */
    int getPort();

    /**
     * The procotol version of this connection
     *
     * @return version enum
     */
    MinecraftProtocol getVersion();

    /**
     * Gets this address as {@link InetSocketAddress}
     * by host and port
     *
     * @return socket address
     */
    InetSocketAddress getAddress();

    /**
     * Checks if this connection is online mode
     *
     * @return boolean
     */
    boolean isOnlineMode();

    /**
     * Im not sure what this is
     *
     * @return boolean
     */
    boolean isLegacy();
}
