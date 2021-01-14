package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudsystem.library.elements.packets.out.service.PacketPlayOutExecuteCommand;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class PacketHandlerBukkitCommand extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerBukkitCommand(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayOutExecuteCommand) {
            PacketPlayOutExecuteCommand packetPlayOutExecuteCommand = (PacketPlayOutExecuteCommand)packet;
            if ((packetPlayOutExecuteCommand.getService().equalsIgnoreCase(cloudAPI.getService().getName()) || packetPlayOutExecuteCommand.getService() == null)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), packetPlayOutExecuteCommand.getExecution());
            }
        }
    }
}
