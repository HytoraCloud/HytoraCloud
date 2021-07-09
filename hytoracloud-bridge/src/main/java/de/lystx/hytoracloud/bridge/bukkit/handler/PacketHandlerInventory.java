package de.lystx.hytoracloud.bridge.bukkit.handler;

import de.lystx.hytoracloud.bridge.bukkit.utils.Item;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketOpenInventory;
import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketInventoryUpdate;

import de.lystx.hytoracloud.driver.service.player.featured.inventory.CloudInventory;
import de.lystx.hytoracloud.driver.service.player.featured.inventory.CloudItem;
import de.lystx.hytoracloud.driver.service.player.featured.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import net.hytora.networking.elements.packet.HytoraPacket;
import net.hytora.networking.elements.packet.handler.PacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PacketHandlerInventory implements PacketHandler {

    
    public void handle(HytoraPacket rawPacket) {
        if (rawPacket instanceof PacketOpenInventory) {
            PacketOpenInventory packet = (PacketOpenInventory)rawPacket;
            CloudInventory inventory = packet.getCloudInventory();
            CloudPlayer player = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(packet.getCloudPlayer().getName());
            Player bukkitPlayer = Bukkit.getPlayer(player.getName());

            Inventory inv = Bukkit.createInventory(bukkitPlayer, inventory.getRows() * 9, inventory.getName());
            inventory.getItems().forEach((slot, item) -> inv.setItem(slot, this.fromCloudItem(item)));
            bukkitPlayer.openInventory(inv);
        } else if (rawPacket instanceof PacketInventoryUpdate) {
            PacketInventoryUpdate packetInventoryUpdate = (PacketInventoryUpdate)rawPacket;
            CloudPlayerInventory playerInventory = packetInventoryUpdate.getPlayerInventory();
            CloudPlayer player = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(packetInventoryUpdate.getCloudPlayer().getName());
            Player bukkitPlayer = Bukkit.getPlayer(player.getName());

            bukkitPlayer.getInventory().setHelmet(this.fromCloudItem(playerInventory.getHelmet()));
            bukkitPlayer.getInventory().setChestplate(this.fromCloudItem(playerInventory.getChestplate()));
            bukkitPlayer.getInventory().setLeggings(this.fromCloudItem(playerInventory.getLeggings()));
            bukkitPlayer.getInventory().setBoots(this.fromCloudItem(playerInventory.getBoots()));
            playerInventory.getSlots().forEach((slot, item) -> bukkitPlayer.getInventory().setItem(slot, this.fromCloudItem(item)));
        }
    }

    /**
     * Transforms a CloudItem to a Bukkit ItemStack
     * @param cloudItem
     * @return
     */
    public ItemStack fromCloudItem(CloudItem cloudItem) {
        if (cloudItem == null) {
            return null;
        }
        Item itemStack = new Item(
                Material.valueOf(cloudItem.getMaterial()),
                cloudItem.getId(),
                cloudItem.getAmount()
        );
        itemStack.addLoreAll(cloudItem.getLore());
        itemStack.setUnbreakable(cloudItem.isUnbreakable());
        if (cloudItem.isGlow()) {
            itemStack.setGlow();
        }
        itemStack.setDisplayName(cloudItem.getDisplayName());
        if (cloudItem.getSkullOwner() != null) {
            itemStack.setSkullOwner(cloudItem.getSkullOwner());
        }
        return itemStack.build();
    }
}
