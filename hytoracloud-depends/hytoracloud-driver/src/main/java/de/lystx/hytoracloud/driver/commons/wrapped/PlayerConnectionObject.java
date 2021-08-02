package de.lystx.hytoracloud.driver.commons.wrapped;


import de.lystx.hytoracloud.driver.cloudservices.managing.player.required.IPlayerConnection;
import de.lystx.hytoracloud.driver.commons.enums.versions.MinecraftProtocol;
import de.lystx.hytoracloud.driver.commons.storage.JsonDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Class is used
 * to disconnect the player
 * and send packets
 */
@Getter @AllArgsConstructor
public class PlayerConnectionObject extends WrappedObject<IPlayerConnection, PlayerConnectionObject> implements IPlayerConnection {


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
     * The address (host)
     */
    private final String host;

    /**
     * The address (port)
     */
    private final int port;

    /**
     * The protocolVersion
     */
    private final MinecraftProtocol version;

    /**
     * If the connection is online (Cracked users)
     */
    private final boolean onlineMode;

    /**
     * If its legacy or not
     */
    private final boolean legacy;

    @Override
    public InetSocketAddress getAddress() {
        return new InetSocketAddress(this.host, this.port);
    }

    @Override
    public String toString() {
        return JsonDocument.GSON.toJson(this);
    }

    @Override
    Class<PlayerConnectionObject> getWrapperClass() {
        return PlayerConnectionObject.class;
    }

    @Override
    Class<IPlayerConnection> getInterface() {
        return IPlayerConnection.class;
    }
}
