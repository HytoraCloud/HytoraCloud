package de.lystx.hytoracloud.launcher.cloud.handler.receiver;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class CloudHandlerReceiverServer implements PacketHandler {

    private final CloudSystem cloudSystem;

    
    public void handle(HytoraPacket packet) {
        if (packet.getClass().getSimpleName().startsWith("PacketIn") && (packet.getClass().getSuperclass() != null && !packet.getClass().getSuperclass().equals(PacketCommunication.class))) {
            this.cloudSystem.sendPacket(packet);
        }
    }


}
