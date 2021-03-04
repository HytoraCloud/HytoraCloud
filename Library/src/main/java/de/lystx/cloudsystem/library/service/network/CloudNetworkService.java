package de.lystx.cloudsystem.library.service.network;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.CloudServiceType;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudServer;
import lombok.Getter;

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
    public void sendPacket(Packet packet) {
        this.cloudServer.sendPacket(packet);
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

        server.start();
    }

    /**
     * Stops server
     */
    public void shutdown() {
        this.cloudServer.disconnect();
    }
}
