package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.events.player.ProxyServerChatEvent;
import de.lystx.cloudsystem.library.elements.packets.both.PacketPlayerChat;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;

@Getter @AllArgsConstructor
public class PacketHandlerProxyChatEvent extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    @Override
    public void handle(Packet packet) {
        if (packet instanceof PacketPlayerChat) {
            PacketPlayerChat packetPlayerChat = (PacketPlayerChat)packet;
            CloudPlayer cloudPlayer = cloudAPI.getCloudPlayers().get(packetPlayerChat.getPlayer());
            if (cloudPlayer == null) {
                return;
            }
            ProxyServer.getInstance().getPluginManager().callEvent(new ProxyServerChatEvent(cloudPlayer, packetPlayerChat.getMessage()));
        }
    }
}
