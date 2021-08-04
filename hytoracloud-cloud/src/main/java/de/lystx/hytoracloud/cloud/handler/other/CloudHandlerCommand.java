package de.lystx.hytoracloud.cloud.handler.other;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.PacketCommand;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.handler.PacketHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class CloudHandlerCommand implements PacketHandler {

    private final CloudSystem cloudSystem;


    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommand) {
            PacketCommand packetCommand = (PacketCommand)packet;
            if (packetCommand.getService().equalsIgnoreCase("null")) {
                cloudSystem.getCommandManager().executeCommand(cloudSystem.getParent().getConsole(), packetCommand.getCommand());
            }
        }
    }
}
