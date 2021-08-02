package de.lystx.hytoracloud.bridge.proxy.global.handler;

import de.lystx.hytoracloud.bridge.CloudBridge;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.bridge.ProxyBridge;
import de.lystx.hytoracloud.driver.commons.packets.both.player.*;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;


import java.util.UUID;
import java.util.function.Consumer;

public class ProxyHandlerCloudPlayer implements PacketHandler {


    public ProxyHandlerCloudPlayer() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                JsonObject<?> document = driverRequest.getDocument();
                if (driverRequest.getKey().equalsIgnoreCase("PLAYER_CONNECT_REQUEST")) {

                    CloudBridge.getInstance().getProxyBridge().connectPlayer(UUID.fromString(document.getString("uniqueId")), document.getString("server"));
                    driverRequest.createResponse(Boolean.class).data(true).send();

                } else if (driverRequest.getKey().equalsIgnoreCase("PLAYER_GET_PING")) {
                    driverRequest.createResponse(Integer.class).data(CloudBridge.getInstance().getProxyBridge().getPing(UUID.fromString(document.getString("uniqueId")))).send();

                } else if (driverRequest.equalsIgnoreCase("PLAYER_SEND_TABLIST")) {
                    String header = document.getString("header");
                    String footer = document.getString("footer");
                    UUID uniqueId = UUID.fromString(document.getString("uniqueId"));
                    CloudBridge.getInstance().getBridgeInstance().sendTabList(uniqueId, header, footer);
                    driverRequest.createResponse(Boolean.class).data(true).send();

                } else if (driverRequest.equalsIgnoreCase("PLAYER_KICK")) {
                    UUID uniqueId = UUID.fromString(document.getString("uniqueId"));
                    String reason = document.getString("reason");
                    CloudBridge.getInstance().getProxyBridge().kickPlayer(uniqueId, reason);
                    driverRequest.createResponse(Boolean.class).data(true).send();

                } else if (driverRequest.equalsIgnoreCase("PLAYER_FALLBACK")) {
                    UUID uniqueId = UUID.fromString(document.getString("uniqueId"));
                    CloudBridge.getInstance().getProxyBridge().fallbackPlayer(uniqueId);
                    driverRequest.createResponse(Boolean.class).data(true).send();

                }
            }
        });
    }

    @Override
    public void handle(Packet packet) {
        ProxyBridge proxyBridge = CloudBridge.getInstance().getProxyBridge();

        if (packet instanceof PacketSendComponent) {
            PacketSendComponent packetSendComponent = (PacketSendComponent)packet;
            proxyBridge.sendComponent(packetSendComponent.getUuid(), packetSendComponent.getChatComponent());

        } else if (packet instanceof PacketSendMessage) {
            PacketSendMessage packetSendMessage = (PacketSendMessage)packet;
            proxyBridge.messagePlayer(packetSendMessage.getUuid(), packetSendMessage.getMessage());
        }
    }


}
