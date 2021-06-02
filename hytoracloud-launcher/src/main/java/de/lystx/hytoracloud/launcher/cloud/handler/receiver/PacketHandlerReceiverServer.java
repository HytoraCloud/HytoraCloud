package de.lystx.hytoracloud.launcher.cloud.handler.receiver;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.elements.packets.out.PacketOutStartedServer;
import de.lystx.hytoracloud.driver.elements.service.Service;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketHandlerReceiverServer implements PacketHandler {

    private final CloudSystem cloudSystem;

    
    public void handle(Packet packet) {
        if (packet.getClass().getSimpleName().startsWith("PacketIn") && (packet.getClass().getSuperclass() != null && !packet.getClass().getSuperclass().equals(PacketCommunication.class))) {
            this.cloudSystem.sendPacket(packet);
        }
    }

    
    public void handle(PacketOutStartedServer packet) {
        Service service = CloudDriver.getInstance().getServiceManager().getService(packet.getService());
        this.cloudSystem.getParent().getConsole().getLogger().sendMessage("NETWORK", "§7The service §b" + service.getName() + " §7is §equeued §7| §b" + service.getServiceGroup().getReceiver() + " §7| §bID " + service.getServiceID() + " §7| §bPort " + service.getPort() + " §7| §bGroup " + service.getServiceGroup().getName() + " §7| §bType " + service.getServiceGroup().getServiceType().name() );
    }

}
