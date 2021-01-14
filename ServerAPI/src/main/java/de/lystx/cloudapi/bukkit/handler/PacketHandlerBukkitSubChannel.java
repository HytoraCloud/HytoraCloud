package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.events.CloudServerSubChannelMessageEvent;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSubMessage;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class PacketHandlerBukkitSubChannel extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerBukkitSubChannel(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketCommunicationSubMessage) {
            PacketCommunicationSubMessage subMessage = (PacketCommunicationSubMessage)packet;
            Bukkit.getPluginManager().callEvent(new CloudServerSubChannelMessageEvent(subMessage.getChannel(), subMessage.getChannel(), subMessage.getDocument()));
        }
    }
}
