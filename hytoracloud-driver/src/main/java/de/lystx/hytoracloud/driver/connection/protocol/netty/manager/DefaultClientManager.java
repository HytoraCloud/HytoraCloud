package de.lystx.hytoracloud.driver.connection.protocol.netty.manager;

import de.lystx.hytoracloud.driver.connection.protocol.netty.other.ClientType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.data.INettyClient;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class DefaultClientManager implements IClientManager {

    /**
     * The connected {@link INettyClient}'s by the type of them
     */
    private final ConcurrentMap<ClientType, Map<InetSocketAddress, INettyClient>> clientsByType;

    public DefaultClientManager() {
        this.clientsByType = new ConcurrentHashMap<>();

        for (ClientType clientType : ClientType.values()) {
            clientsByType.put(clientType, new HashMap<>());
        }
    }

    @Override
    public void registerClient(INettyClient networkClient) {
        Map<InetSocketAddress, INettyClient> map = clientsByType.get(networkClient.getType());
        map.put(networkClient.getAddress(), networkClient);
    }


    @Override
    public void unregisterClient(INettyClient client) {
        for (Map<InetSocketAddress, INettyClient> m : clientsByType.values()) {
            m.entrySet().removeIf(entry -> entry.getKey().equals(client.getAddress()));
        }
    }

    @Override
    public INettyClient getClient(InetSocketAddress address) {
        INettyClient client = null;
        for(Map.Entry<ClientType, Map<InetSocketAddress, INettyClient>> entry : clientsByType.entrySet()) {
            if(entry.getValue().containsKey(address)) {
                client = entry.getValue().get(address);
            }
        }
        return client;
    }

    @Override
    public boolean isRegistered(InetSocketAddress address) {
        return getClient(address) != null;
    }

    @Override
    public INettyClient getClient(String username) {
        return this.getConnectedClients().stream().filter(client -> client.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    @Override
    public List<INettyClient> getClients(ClientType type) {
        Map<InetSocketAddress, INettyClient> map = clientsByType.get(type);

        return new LinkedList<>(map.values());
    }

    @Override
    public List<INettyClient> getConnectedClients() {
        List<INettyClient> clients = new ArrayList<>();
        for(ClientType clientType : ClientType.values()) {
            clients.addAll(getClients(clientType));
        }
        return clients;
    }

}
