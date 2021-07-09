package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.ProxyBridge;
import de.lystx.hytoracloud.driver.elements.packets.both.player.*;
import de.lystx.hytoracloud.driver.elements.packets.both.service.PacketConnectServer;
import de.lystx.hytoracloud.driver.elements.packets.request.other.PacketRequestPing;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import net.hytora.networking.elements.packet.response.ResponseStatus;


import java.util.UUID;

public class ProxyHandlerCloudPlayer implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        ProxyBridge proxyBridge = CloudBridge.getInstance().getProxyBridge();

       if (packet instanceof PacketRequestPing) {
            PacketRequestPing packetOutPingRequest = (PacketRequestPing)packet;
            UUID uuid = packetOutPingRequest.getUuid();
            packet.reply(ResponseStatus.SUCCESS, proxyBridge.getPing(uuid));

        } else if (packet instanceof PacketKickPlayer) {
           PacketKickPlayer packetKickPlayer = (PacketKickPlayer)packet;
           proxyBridge.kickPlayer(packetKickPlayer.getUuid(), packetKickPlayer.getReason());

        } else if (packet instanceof PacketConnectServer) {
            PacketConnectServer packetConnectServer = (PacketConnectServer)packet;
            proxyBridge.connectPlayer(packetConnectServer.getUuid(), packetConnectServer.getServer());

       } else if (packet instanceof PacketSendComponent) {
            PacketSendComponent packetSendComponent = (PacketSendComponent)packet;
            proxyBridge.sendComponent(packetSendComponent.getUuid(), packetSendComponent.getCloudComponent());

        } else if (packet instanceof PacketFallback) {
            PacketFallback packetFallback = (PacketFallback)packet;
            proxyBridge.fallbackPlayer(packetFallback.getUuid());

        } else if (packet instanceof PacketSendMessage) {
            PacketSendMessage packetSendMessage = (PacketSendMessage)packet;
            proxyBridge.messagePlayer(packetSendMessage.getUuid(), packetSendMessage.getMessage());
        }
    }


}
