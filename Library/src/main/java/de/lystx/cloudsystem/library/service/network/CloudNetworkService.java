package de.lystx.cloudsystem.library.service.network;

import de.lystx.cloudsystem.library.CloudLibrary;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketPlayInRemoveNPC;
import de.lystx.cloudsystem.library.service.CloudService;
import de.lystx.cloudsystem.library.service.config.ConfigService;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.defaults.CloudServer;
import lombok.Getter;

@Getter
public class CloudNetworkService extends CloudService {

    private final CloudServer cloudServer;

    public CloudNetworkService(CloudLibrary cloudLibrary, String name, Type type) {
        super(cloudLibrary, name, type);
        this.cloudServer = cloudLibrary.getCloudServer();
        this.start();
    }

    public void sendPacket(Packet packet) {
        this.cloudServer.sendPacket(packet);
    }

    public void registerHandler(PacketHandlerAdapter packetHandlerAdapter) {
        this.cloudServer.registerPacketHandler(packetHandlerAdapter);
    }

    public void start() {
        this.cloudServer.registerPacket((byte) 0, PacketPlayInRemoveNPC.class);
        this.cloudServer.connect(this.getCloudLibrary().getService(ConfigService.class).getNetworkConfig().getHost(), this.getCloudLibrary().getService(ConfigService.class).getNetworkConfig().getPort());
    }

    public void shutdown() {
        this.cloudServer.disconnect();
    }
}
