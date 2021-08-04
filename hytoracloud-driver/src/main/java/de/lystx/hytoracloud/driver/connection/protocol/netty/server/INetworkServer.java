package de.lystx.hytoracloud.driver.connection.protocol.netty.server;

import de.lystx.hytoracloud.driver.connection.protocol.netty.INetworkConnection;
import de.lystx.hytoracloud.driver.connection.protocol.netty.manager.DefaultClientManager;
import de.lystx.hytoracloud.driver.connection.protocol.netty.manager.IClientManager;

public interface INetworkServer extends INetworkConnection {

    /**
     * The {@link DefaultClientManager} to manage clients
     */
    IClientManager getClientManager();
}
