package de.lystx.cloudapi.proxy.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.proxy.CloudProxy;
import de.lystx.cloudapi.proxy.events.player.ProxyServerPlayerRankUpdateEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerPermissionGroupAddEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerPermissionGroupRemoveEvent;
import de.lystx.cloudsystem.library.elements.packets.both.PacketCallEvent;
import de.lystx.cloudsystem.library.service.network.packet.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketHandlerProxyEvent {

    private final CloudAPI cloudAPI;

    @PacketHandler
    public void handlEvent(PacketCallEvent packet) {
        switch (packet.getEvent().getClass().getSimpleName()) {
            case "CloudPlayerPermissionGroupAddEvent":
                CloudPlayerPermissionGroupAddEvent event = (CloudPlayerPermissionGroupAddEvent) packet.getEvent();
                CloudProxy.getInstance().getProxy().getPluginManager().callEvent(new ProxyServerPlayerRankUpdateEvent(this.cloudAPI.getCloudPlayers().get(event.getName()), event.getPermissionGroup(), true));
                break;
            case "CloudPlayerPermissionGroupRemoveEvent":
                CloudPlayerPermissionGroupRemoveEvent playerPermissionGroupRemoveEvent = (CloudPlayerPermissionGroupRemoveEvent)packet.getEvent();
                CloudProxy.getInstance().getProxy().getPluginManager().callEvent(new ProxyServerPlayerRankUpdateEvent(this.cloudAPI.getCloudPlayers().get(playerPermissionGroupRemoveEvent.getPlayerName()), playerPermissionGroupRemoveEvent.getPermissionGroup(), false));
                break;
            default:
        }

    }
}
