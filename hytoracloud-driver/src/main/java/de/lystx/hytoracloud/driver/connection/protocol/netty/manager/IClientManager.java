package de.lystx.hytoracloud.driver.connection.protocol.netty.manager;

import de.lystx.hytoracloud.driver.connection.protocol.netty.client.data.INettyClient;
import de.lystx.hytoracloud.driver.connection.protocol.netty.other.ClientType;

import java.net.InetSocketAddress;
import java.util.List;

public interface IClientManager {

    /**
     * Registers a client in cache
     *
     * @param client the client
     */
    void registerClient(INettyClient client);

    /**
     * Unregisters a client from cache
     *
     * @param client the client
     */
    void unregisterClient(INettyClient client);

    /**
     * Checks if a client with the same address
     * is already registered in cache
     *
     * @param address the address
     * @return boolean
     */
    boolean isRegistered(InetSocketAddress address);

    /**
     * Tries to get a client by username
     *
     * @param username the name
     * @return client or null if not found
     */
    INettyClient getClient(String username);

    /**
     * Tries to get a client by address
     *
     * @param address the address
     * @return client or null if not found
     */
    INettyClient getClient(InetSocketAddress address);

    /**
     * Gets a list of all connected clients
     * that match the same provided {@link ClientType}
     *
     * @param type the type
     * @return list
     */
    List<INettyClient> getClients(ClientType type);

    /**
     * Gets a list of all connected clients
     *
     * @return list
     */
    List<INettyClient> getConnectedClients();
}
