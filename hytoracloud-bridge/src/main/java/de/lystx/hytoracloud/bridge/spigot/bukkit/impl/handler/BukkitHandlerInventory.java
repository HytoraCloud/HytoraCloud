package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler;

import de.lystx.hytoracloud.bridge.spigot.bukkit.utils.BukkitItem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketInventoryUpdate;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitHandlerInventory implements PacketHandler {

    
    public void handle(Packet rawPacket) {
        if (rawPacket instanceof PacketInventoryUpdate) {
            PacketInventoryUpdate packetInventoryUpdate = (PacketInventoryUpdate)rawPacket;
            CloudPlayerInventory playerInventory = packetInventoryUpdate.getPlayerInventory();
            ICloudPlayer player = CloudDriver.getInstance().getPlayerManager().getCachedObject(packetInventoryUpdate.getCloudPlayer().getName());
            Player bukkitPlayer = Bukkit.getPlayer(player.getName());

            bukkitPlayer.getInventory().setHelmet(BukkitItem.fromCloudItem(playerInventory.getHelmet()));
            bukkitPlayer.getInventory().setChestplate(BukkitItem.fromCloudItem(playerInventory.getChestplate()));
            bukkitPlayer.getInventory().setLeggings(BukkitItem.fromCloudItem(playerInventory.getLeggings()));
            bukkitPlayer.getInventory().setBoots(BukkitItem.fromCloudItem(playerInventory.getBoots()));
            playerInventory.getSlots().forEach((slot, item) -> bukkitPlayer.getInventory().setItem(slot, BukkitItem.fromCloudItem(item)));
        }
    }

}
