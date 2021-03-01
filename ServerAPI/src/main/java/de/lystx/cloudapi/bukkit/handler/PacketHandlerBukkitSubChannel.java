package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.events.network.CloudServerPacketReceiveEvent;
import de.lystx.cloudapi.bukkit.events.network.CloudServerSubChannelMessageEvent;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCommunicationSubMessage;
import de.lystx.cloudsystem.library.elements.service.ServiceType;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import lombok.Getter;
import org.bukkit.Bukkit;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@Getter
public class PacketHandlerBukkitSubChannel extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    public PacketHandlerBukkitSubChannel(CloudAPI cloudAPI) {
        this.cloudAPI = cloudAPI;
    }

    @Override
    public void handle(Packet packet) {
        try {
            Bukkit.getPluginManager().callEvent(new CloudServerPacketReceiveEvent(packet));
            if (packet instanceof PacketCommunicationSubMessage) {
                PacketCommunicationSubMessage subMessage = (PacketCommunicationSubMessage)packet;
                if (!subMessage.getType().equals(ServiceType.SPIGOT)) {
                    return;
                }
                Bukkit.getPluginManager().callEvent(new CloudServerSubChannelMessageEvent(subMessage.getChannel(), subMessage.getChannel(), subMessage.getDocument()));

            }
        } catch (IllegalStateException e){}
    }
}
