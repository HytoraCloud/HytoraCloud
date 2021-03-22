package de.lystx.cloudsystem.library.service.network;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.connection.packet.PacketState;
import de.lystx.cloudsystem.library.service.network.defaults.CloudServer;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
public class CloudNetworkService extends CloudService {

    private final CloudServer cloudServer;

    public CloudNetworkService(CloudLibrary cloudLibrary, String name, CloudServiceType type) {
        super(cloudLibrary, name, type);
        this.cloudServer = cloudLibrary.getCloudServer();
        this.start();
    }

    /**
     * Sends a packet
     * @param packet
     */
    public void sendPacket(Packet packet, Consumer<PacketState> stateConsumer) {
        this.cloudServer.sendPacket(packet, stateConsumer);
    }

    /**
     * Sends a packet
     * @param packet
     */
    public void sendPacket(Packet packet) {
        this.sendPacket(packet, null);
    }

    /**
     * Registers a class object
     * @param packetHandlerAdapter
     */
    public void registerHandler(Object packetHandlerAdapter) {
        this.cloudServer.registerPacketHandler(packetHandlerAdapter);
    }

    /**
     * Starts server in thread
     */
    public void start() {
        Thread server = new Thread(() -> {
            this.cloudServer.connect(this.getCloudLibrary().getService(ConfigService.class).getNetworkConfig().getHost(), this.getCloudLibrary().getService(ConfigService.class).getNetworkConfig().getPort());
        }, "hytoraCloud_cloudNetwork");
        Constants.EXECUTOR = this.cloudServer;
        server.start();
    }

    /**
     * Stops server
     */
    public void shutdown() {
        this.cloudServer.disconnect();
    }
}
