package de.lystx.cloudsystem.library.service.network.defaults;

import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.service.network.netty.NettyClient;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Setter @Getter
public class CloudClient extends NettyClient implements CloudExecutor {


    private final List<NetworkHandler> networkHandlers;

    public CloudClient(String hostname, int port) {
        super(hostname, port);
        this.networkHandlers = new LinkedList<>();
    }


    public void connect() throws IOException {
        this.start();
    }

    public void connect(String host, int port) throws IOException {
        this.setHostname(host);
        this.setPort(port);
        this.connect();
    }

    public boolean isConnected() {
        return this.isRunning();
    }

    public void disconnect() {
        this.getChannel().close();
    }

    public void registerPacketHandler(Object packetHandlerAdapter) {
        this.getPacketAdapter().registerAdapter(packetHandlerAdapter);
    }

    public void registerHandler(NetworkHandler networkHandler) {
        this.networkHandlers.add(networkHandler);
    }

    public List<NetworkHandler> getNetworkHandlers() {
        return networkHandlers;
    }
}
