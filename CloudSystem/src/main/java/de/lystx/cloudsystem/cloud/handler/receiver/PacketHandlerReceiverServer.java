package de.lystx.cloudsystem.cloud.handler.receiver;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketOutRegisterServer;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketOutStartedServer;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketHandlerReceiverServer extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet.getClass().getSimpleName().startsWith("PacketIn")) {
            this.cloudSystem.sendPacket(packet);
        }
    }

    @PacketHandler
    public void handle(PacketOutStartedServer packet) {
        Service service = packet.getService();
        this.cloudSystem.getConsole().getLogger().sendMessage("NETWORK", "§7The service §b" + service.getName() + " §7is §equeued §7| §b" + service.getServiceGroup().getReceiver() + " §7| §bID " + service.getServiceID() + " §7| §bPort " + service.getPort() + " §7| §bGroup " + service.getServiceGroup().getName() + " §7| §bType " + service.getServiceGroup().getServiceType().name() );
    }

}
