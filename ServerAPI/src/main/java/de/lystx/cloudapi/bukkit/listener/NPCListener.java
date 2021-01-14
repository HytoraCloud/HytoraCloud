package de.lystx.cloudapi.bukkit.listener;

import afu.org.checkerframework.checker.igj.qual.I;
import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.events.CloudServerNPCInteractEvent;
import de.lystx.cloudapi.bukkit.manager.Item;
import de.lystx.cloudapi.bukkit.manager.npc.impl.NPC;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.serverselector.sign.manager.ServerPinger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

public class NPCListener implements Listener {

    @EventHandler
    public void onInteract(CloudServerNPCInteractEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getPlayer();
        String group = CloudServer.getInstance().getNpcManager().getGroupNPCS().get(npc);
        if (group == null) {
            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cCan't handle NPC because group was not found!");
            return;
        }
        player.openInventory(this.getInventory(player, group));
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        if (event.getCurrentItem().getItemMeta() == null) {
            return;
        }
        if (event.getRawSlot() > event.getInventory().getSize()) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        if (player.getOpenInventory() != null && player.getOpenInventory().getTitle().startsWith("§8» §7Group §8┃ §b")) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType().equals(Material.STORAGE_MINECART)) {
                String service = (event.getCurrentItem().getItemMeta().getDisplayName().split(" "))[1];
                service = service.replace("§8» §b", "");
                CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(player.getName());
                if (cloudPlayer == null) {
                    player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cCouldn't find you in global CloudPlayers!");
                    return;
                }
                cloudPlayer.sendMessage(CloudAPI.getInstance().getCloudClient(), CloudAPI.getInstance().getPrefix() + "§7Connecting to §b" + service);
                cloudPlayer.sendToServer(CloudAPI.getInstance().getCloudClient(), service);
            }
        }
    }


    public Inventory getInventory(Player player, String group) {
        ServiceGroup serviceGroup = CloudAPI.getInstance().getNetwork().getServiceGroup(group);
        Inventory inventory = Bukkit.createInventory(player, 6*9, "§8» §7Group §8┃ §b" + serviceGroup.getName());
        ItemStack glass = new Item(Material.STAINED_GLASS_PANE, (short) 7).setNoName().build();

        IntStream.range(0, 8).forEach(i -> {
            inventory.setItem(i, glass);
        });

        for (int s = 8; s < (inventory.getSize() - 9); s += 9) {
            int lastSlot = s + 1;
            inventory.setItem(s, glass);
            inventory.setItem(lastSlot, glass);

        }
        for (int lr = (inventory.getSize() - 9); lr < inventory.getSize(); lr++) {
            inventory.setItem(lr, glass);
        }

        List<Service> list = CloudAPI.getInstance().getNetwork().getServices(serviceGroup);
        inventory.setItem(4, new Item(Material.NAME_TAG).setDisplayName("§8» §3" + serviceGroup.getName())
                .addLoreLine("§8")
                .addLoreLine("§8§m-----------")
                .addLoreLine("§8» §bOnline §8» §7" + list.size())
                .addLoreLine("§8» §bType §8» §7" + serviceGroup.getServiceType())
                .addLoreLine("§8» §bTemplate §8» §7" + serviceGroup.getTemplate())
                .build());
        for (Service service : list) {
            ServerPinger serverPinger = new ServerPinger();
            try {
                serverPinger.pingServer(service.getHost(), service.getPort(), 5);
                ItemStack itemStack = new Item(Material.STORAGE_MINECART).setDisplayName("§8» §b" + service.getName() + " §8[" + service.getServiceState().getColor() + service.getServiceState().name() + "§8]")
                        .addLoreLine("§8")
                        .addLoreLine("§8§m-----------")
                        .addLoreLine("§8» §bOnline §8» §7" + serverPinger.getPlayers())
                        .addLoreLine("§8» §bMaxPlayers §8» §7" + serverPinger.getMaxplayers())
                        .addLoreLine("§8» §bMotd §8» §7" + serverPinger.getMotd())
                        .build();
                inventory.addItem(itemStack);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return inventory;
    }
}
