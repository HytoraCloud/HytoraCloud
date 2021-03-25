package de.lystx.cloudsystem.library.service.network.defaults;

import de.lystx.cloudsystem.library.elements.interfaces.NetworkHandler;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.packet.PacketState;
import de.lystx.cloudsystem.library.service.network.netty.NettyClient;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@Setter @Getter
public class CloudClient extends NettyClient implements CloudExecutor {


    private final List<NetworkHandler> networkHandlers;

    public CloudClient(String hostname, int port) {
        super(hostname, port);
        this.networkHandlers = new LinkedList<>();
    }

    /**
     * COnnects
     * @throws Exception
     */
    public void connect() throws Exception {
        this.start();
    }

    /**
     * Connect with custom host and port
     * @param host
     * @param port
     * @throws Exception
     */
    public void connect(String host, int port) throws Exception {
        this.setHost(host);
        this.setPort(port);
        this.connect();
    }

    /**
     * @return if connected
     */
    public boolean isConnected() {
        return this.isRunning();
    }

    /**
     * Disconnecting
     */
    public void disconnect() {
        this.getChannel().close();
    }

    /**
     * Registers handler
     * @param packetHandlerAdapter
     */
    public void registerPacketHandler(Object packetHandlerAdapter) {
        this.getPacketAdapter().registerAdapter(packetHandlerAdapter);
    }

    /**
     * Registers multiple PacketHandlers
     * @param packetHandlerAdapter
     */
    public void registerPacketHandlers(Object... packetHandlerAdapter) {
        for (Object o : packetHandlerAdapter) {
            this.registerPacketHandler(o);
        }
    }

    /**
     * registers networkHandler
     * @param networkHandler
     */
    public void registerHandler(NetworkHandler networkHandler) {
        this.networkHandlers.add(networkHandler);
    }

    @Override
    public void sendPacket(Packet packet, Consumer<PacketState> consumer) {
        super.sendPacket(packet, consumer);
    }
}
