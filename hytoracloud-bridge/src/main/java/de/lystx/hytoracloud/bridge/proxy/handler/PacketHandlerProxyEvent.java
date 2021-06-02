package de.lystx.hytoracloud.bridge.proxy.handler;

import de.lystx.hytoracloud.bridge.proxy.CloudProxy;
import de.lystx.hytoracloud.bridge.proxy.events.player.ProxyServerPlayerRankUpdateEvent;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.events.player.CloudPlayerPermissionGroupAddCloudEvent;
import de.lystx.hytoracloud.driver.elements.events.player.CloudPlayerPermissionGroupRemoveCloudEvent;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketCallEvent;
import io.thunder.packet.Packet;
import io.thunder.packet.handler.PacketHandler;

public class PacketHandlerProxyEvent implements PacketHandler {

    @Override
    public void handle(Packet packet) {
        if (!(packet instanceof PacketCallEvent)) {
            return;

        }
        PacketCallEvent packetCallEvent = (PacketCallEvent)packet;
        switch (packetCallEvent.getCloudEvent().getClass().getSimpleName()) {
            case "CloudPlayerPermissionGroupAddEvent":
                CloudPlayerPermissionGroupAddCloudEvent event = (CloudPlayerPermissionGroupAddCloudEvent) packetCallEvent.getCloudEvent();
                CloudProxy.getInstance().getProxy().getPluginManager().callEvent(new ProxyServerPlayerRankUpdateEvent(CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(event.getName()), event.getPermissionGroup(), true));
                break;
            case "CloudPlayerPermissionGroupRemoveEvent":
                CloudPlayerPermissionGroupRemoveCloudEvent playerPermissionGroupRemoveEvent = (CloudPlayerPermissionGroupRemoveCloudEvent)packetCallEvent.getCloudEvent();
                CloudProxy.getInstance().getProxy().getPluginManager().callEvent(new ProxyServerPlayerRankUpdateEvent(CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(playerPermissionGroupRemoveEvent.getPlayerName()), playerPermissionGroupRemoveEvent.getPermissionGroup(), false));
                break;
            default:
        }

    }
}
