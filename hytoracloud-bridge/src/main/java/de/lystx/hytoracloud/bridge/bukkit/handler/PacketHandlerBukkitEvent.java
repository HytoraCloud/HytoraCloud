package de.lystx.hytoracloud.bridge.bukkit.handler;

import de.lystx.hytoracloud.bridge.bukkit.HytoraCloudBukkitBridge;
import de.lystx.hytoracloud.bridge.bukkit.events.player.CloudServerPlayerRankUpdateEvent;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.events.player.CloudPlayerPermissionGroupAddCloudEvent;
import de.lystx.hytoracloud.driver.elements.events.player.CloudPlayerPermissionGroupRemoveCloudEvent;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketCallEvent;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;

public class PacketHandlerBukkitEvent implements PacketHandler {


    @Override
    public void handle(HytoraPacket packet) {
        if (!(packet instanceof PacketCallEvent)) {
            return;
        }

        PacketCallEvent packetCallEvent = (PacketCallEvent)packet;
        switch (packetCallEvent.getCloudEvent().getClass().getSimpleName()) {
            case "CloudPlayerPermissionGroupAddEvent":
                CloudPlayerPermissionGroupAddCloudEvent event = (CloudPlayerPermissionGroupAddCloudEvent) packetCallEvent.getCloudEvent();
                HytoraCloudBukkitBridge.getInstance().getServer().getPluginManager().callEvent(new CloudServerPlayerRankUpdateEvent(CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(event.getName()), event.getPermissionGroup(), true));
                break;
            case "CloudPlayerPermissionGroupRemoveEvent":
                CloudPlayerPermissionGroupRemoveCloudEvent playerPermissionGroupRemoveEvent = (CloudPlayerPermissionGroupRemoveCloudEvent)packetCallEvent.getCloudEvent();
                HytoraCloudBukkitBridge.getInstance().getServer().getPluginManager().callEvent(new CloudServerPlayerRankUpdateEvent(CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(playerPermissionGroupRemoveEvent.getPlayerName()), playerPermissionGroupRemoveEvent.getPermissionGroup(), false));
                break;
            default:
        }

    }
}
