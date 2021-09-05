package de.lystx.hytoracloud.driver.connection.protocol.netty.server;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.server.manager.DefaultClientManager;
import de.lystx.hytoracloud.driver.connection.protocol.netty.server.manager.IClientManager;

public interface INetworkServer extends INetworkConnection {

    /**
     * The {@link DefaultClientManager} to manage clients
     */
    IClientManager getClientManager();
}
