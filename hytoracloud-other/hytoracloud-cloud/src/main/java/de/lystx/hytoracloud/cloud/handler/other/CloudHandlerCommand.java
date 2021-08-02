package de.lystx.hytoracloud.cloud.handler.other;

import de.lystx.hytoracloud.cloud.CloudSystem;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommand;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

import de.lystx.hytoracloud.driver.cloudservices.managing.command.CommandService;
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
                cloudSystem.getInstance(CommandService.class).execute(packetCommand.getCommand(), cloudSystem.getParent().getConsole());
            }
        }
    }
}
