package de.lystx.hytoracloud.cloud.handler.managing;

import de.lystx.hytoracloud.cloud.CloudSystem;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketLogMessage;
import de.lystx.hytoracloud.driver.cloudservices.cloud.log.LogService;
import lombok.AllArgsConstructor;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;
import de.lystx.hytoracloud.networking.elements.packet.response.ResponseStatus;

@AllArgsConstructor
public class CloudHandlerMessage implements PacketHandler {

    private final CloudSystem cloudSystem;


    
    public void handle(Packet packet) {
        if (packet instanceof PacketLogMessage) {
            PacketLogMessage packetLogMessage = (PacketLogMessage)packet;
            if (!packetLogMessage.isShowUpInConsole()) {
                packet.reply(ResponseStatus.CONFLICT, "Not show up in console");
                this.cloudSystem.getInstance(LogService.class).log(packetLogMessage.getPrefix(), packetLogMessage.getMessage());
                return;
            }
            this.cloudSystem.getParent().getConsole().getLogger().sendMessage(packetLogMessage.getPrefix(), packetLogMessage.getMessage());
            packet.reply(ResponseStatus.SUCCESS, "Showed up in console");
        }
    }
}
