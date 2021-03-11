package de.lystx.cloudapi.bukkit.handler;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.utils.Item;
import de.lystx.cloudsystem.library.elements.packets.both.PacketOpenInventory;
import de.lystx.cloudsystem.library.elements.packets.both.PacketInventoryUpdate;
import de.lystx.cloudsystem.library.service.network.connection.adapter.PacketHandlerAdapter;
import de.lystx.cloudsystem.library.service.network.connection.packet.Packet;
import de.lystx.cloudsystem.library.service.network.packet.raw.PacketHandler;
import de.lystx.cloudsystem.library.service.player.featured.CloudInventory;
import de.lystx.cloudsystem.library.service.player.featured.CloudItem;
import de.lystx.cloudsystem.library.service.player.featured.CloudPlayerInventory;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Getter @AllArgsConstructor
public class PacketHandlerInventory extends PacketHandlerAdapter {

    private final CloudAPI cloudAPI;

    @Override
    public void handle(Packet rawPacket) {
        if (rawPacket instanceof PacketOpenInventory) {
            PacketOpenInventory packet = (PacketOpenInventory)rawPacket;
            CloudInventory inventory = packet.getCloudInventory();
            CloudPlayer player = CloudAPI.getInstance().getCloudPlayers().get(packet.getCloudPlayer().getName());
            Player bukkitPlayer = Bukkit.getPlayer(player.getName());

            Inventory inv = Bukkit.createInventory(bukkitPlayer, inventory.getRows() * 9, inventory.getName());
            inventory.getItems().forEach((slot, item) -> inv.setItem(slot, this.fromCloudItem(item)));
            bukkitPlayer.openInventory(inv);
        }
    }


    @PacketHandler
    public void handle(PacketInventoryUpdate packet) {
        CloudPlayerInventory playerInventory = packet.getPlayerInventory();
        CloudPlayer player = CloudAPI.getInstance().getCloudPlayers().get(packet.getCloudPlayer().getName());
        Player bukkitPlayer = Bukkit.getPlayer(player.getName());

        bukkitPlayer.getInventory().setHelmet(this.fromCloudItem(playerInventory.getHelmet()));
        bukkitPlayer.getInventory().setChestplate(this.fromCloudItem(playerInventory.getChestplate()));
        bukkitPlayer.getInventory().setLeggings(this.fromCloudItem(playerInventory.getLeggings()));
        bukkitPlayer.getInventory().setBoots(this.fromCloudItem(playerInventory.getBoots()));
        playerInventory.getSlots().forEach((slot, item) -> bukkitPlayer.getInventory().setItem(slot, this.fromCloudItem(item)));
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
