package de.lystx.cloudsystem.cloud.handler.other;

import de.lystx.cloudsystem.cloud.CloudSystem;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketCommand;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.command.CommandService;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;


public class PacketHandlerCommand extends PacketHandlerAdapter {

    private final CloudSystem cloudSystem;

    public PacketHandlerCommand(CloudSystem cloudSystem) {
        this.cloudSystem = cloudSystem;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommand) {
            PacketCommand packetPlayInCommand = (PacketCommand)packet;
            if (((String)packetPlayInCommand.get("service")).equalsIgnoreCase("null")) {
                cloudSystem.getService(CommandService.class).execute((String) packetPlayInCommand.get("command"), cloudSystem.getConsole());
            }
        }
    }
}
