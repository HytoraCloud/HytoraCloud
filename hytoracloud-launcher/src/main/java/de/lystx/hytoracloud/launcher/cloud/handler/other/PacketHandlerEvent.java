package de.lystx.hytoracloud.launcher.cloud.handler.other;

import de.lystx.hytoracloud.launcher.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketCallEvent;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.service.event.DefaultEventService;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PacketHandlerEvent implements PacketHandler {

    private final CloudSystem cloudSystem;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCallEvent) {
            PacketCallEvent packetCallEvent = (PacketCallEvent)packet;
            this.cloudSystem.getInstance(DefaultEventService.class).callEvent(packetCallEvent.getCloudEvent());
        }
    }
}
