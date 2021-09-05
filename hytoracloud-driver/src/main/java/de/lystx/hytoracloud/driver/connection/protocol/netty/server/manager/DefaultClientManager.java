package de.lystx.hytoracloud.driver.connection.protocol.netty.server.manager;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.identification.ConnectionType;
import de.lystx.hytoracloud.driver.connection.protocol.netty.client.data.INettyClient;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class DefaultClientManager implements IClientManager {

    /**
     * The connected {@link INettyClient}'s
     */
    private final List<INettyClient> connectedClients;

    public DefaultClientManager() {
        this.connectedClients = new LinkedList<>();

    }

    @Override
    public void registerClient(INettyClient networkClient) {
        this.connectedClients.add(networkClient);
    }

    @Override
    public void unregisterClient(INettyClient client) {
        for (INettyClient nettyClient : new LinkedList<>(this.connectedClients)) {
            if (nettyClient.getUsername().equalsIgnoreCase(client.getUsername()) && nettyClient.getType().equals(client.getType())) {
                this.connectedClients.remove(nettyClient);
            }
        }
    }

    @Override
    public INettyClient getClient(InetSocketAddress address) {
        return this.getConnectedClients().stream().filter(iNettyClient -> iNettyClient.getChannel().remoteAddress().equals(address)).findFirst().orElse(null);
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
    public List<INettyClient> getClients(ConnectionType type) {
        return this.getConnectedClients().stream().filter(iNettyClient -> iNettyClient.getType().equals(type)).collect(Collectors.toList());
    }

}
