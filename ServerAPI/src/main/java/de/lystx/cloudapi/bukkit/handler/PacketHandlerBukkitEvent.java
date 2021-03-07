package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.events.player.CloudServerPlayerRankUpdateEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerPermissionGroupAddEvent;
import de.lystx.cloudsystem.library.elements.events.player.CloudPlayerPermissionGroupRemoveEvent;
import de.lystx.cloudsystem.library.elements.packets.communication.PacketCallEvent;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketHandlerBukkitEvent {

    private final CloudAPI cloudAPI;

    @PacketHandler
    public void handlEvent(PacketCallEvent packet) {
        switch (packet.getEvent().getClass().getSimpleName()) {
            case "CloudPlayerPermissionGroupAddEvent":
                CloudPlayerPermissionGroupAddEvent event = (CloudPlayerPermissionGroupAddEvent) packet.getEvent();
                CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerPlayerRankUpdateEvent(this.cloudAPI.getCloudPlayers().get(event.getName()), event.getPermissionGroup(), true));
                break;
            case "CloudPlayerPermissionGroupRemoveEvent":
                CloudPlayerPermissionGroupRemoveEvent playerPermissionGroupRemoveEvent = (CloudPlayerPermissionGroupRemoveEvent)packet.getEvent();
                CloudServer.getInstance().getServer().getPluginManager().callEvent(new CloudServerPlayerRankUpdateEvent(this.cloudAPI.getCloudPlayers().get(playerPermissionGroupRemoveEvent.getPlayerName()), playerPermissionGroupRemoveEvent.getPermissionGroup(), false));
                break;
            default:
        }

    }
}