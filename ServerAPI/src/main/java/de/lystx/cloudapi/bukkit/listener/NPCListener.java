package de.lystx.cloudapi.bukkit.listener;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.events.other.CloudServerNPCInteractEvent;
import de.lystx.cloudapi.bukkit.utils.Item;
import de.lystx.cloudapi.bukkit.manager.npc.impl.NPC;
import de.lystx.cloudsystem.library.elements.service.GroupInfo;
import de.lystx.cloudsystem.library.elements.service.Service;
import de.lystx.cloudsystem.library.elements.service.ServiceGroup;
import de.lystx.cloudsystem.library.elements.service.ServiceInfo;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCConfig;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.IntStream;

@Getter
public class NPCListener implements Listener {

    private final Map<UUID, Map<Integer, Service>> services;

    public NPCListener() {
        this.services = new HashMap<>();
    }



    @EventHandler
    public void onInteract(CloudServerNPCInteractEvent event) {
        NPC npcV18R3V18R3 = event.getNPC();
        Player player = event.getPlayer();
        String group = CloudServer.getInstance().getNpcManager().getGroupNPCS().get(npcV18R3V18R3);
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
        Map<Integer, Service> serviceMap = this.services.getOrDefault(player.getUniqueId(), new HashMap<>());
        if (player.getOpenInventory() != null && !serviceMap.isEmpty()) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType().equals(Material.valueOf(CloudServer.getInstance().getNpcManager().getNpcConfig().getItemType()))) {
                CloudPlayer cloudPlayer = CloudAPI.getInstance().getCloudPlayers().get(player.getName());
                if (cloudPlayer == null) {
                    player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cCouldn't find you in global CloudPlayers!");
                    return;
                }
                Service service = serviceMap.get(event.getSlot());
                if (!CloudServer.getInstance().getNpcManager().getNpcConfig().getConnectingMessage().trim().isEmpty()) {
                    player.sendMessage(this.replace(CloudServer.getInstance().getNpcManager().getNpcConfig().getConnectingMessage(), service, service.getServiceGroup()));
                }
                cloudPlayer.connect(service.getName());
            }
        }
    }



    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        this.services.remove(event.getPlayer().getUniqueId());
    }

    public Inventory getInventory(Player player, String group) {
        Map<Integer, Service> serviceMap = new HashMap<>();
        NPCConfig config = CloudServer.getInstance().getNpcManager().getNpcConfig();
        ServiceGroup serviceGroup = CloudAPI.getInstance().getNetwork().getServiceGroup(group);
        if (serviceGroup == null) {
            player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThere was an error!");
            return null;
        }
        Inventory inventory = Bukkit.createInventory(player, (config.getInventoryRows() * 9), this.replace(config.getInventoryTitle(), null, serviceGroup));
        if (config.isCorners()) {
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
        }
        List<Service> list = CloudAPI.getInstance().getNetwork().getServices(serviceGroup);

        for (VsonObject document : config.getItems()) {
            if (document.isEmpty()) {
                continue;
            }
            List<String> lore = document.getList("lore");
            for (String s : lore) {
                lore.set(lore.indexOf(s), this.replace(s, null, serviceGroup));
            }
            int slot = document.getInteger("slot", 0);
            inventory.setItem(slot, new Item(Material.valueOf(document.getString("type")))
                    .setDisplayName(this.replace(document.getString("name"), null, serviceGroup))
                    .addLoreAll(lore)
                    .build());
        }

        for (Service service : list) {
            List<String> lore = config.getLore();
            for (String s : lore) {
                lore.set(lore.indexOf(s), this.replace(s, service, serviceGroup));
            }
            ItemStack itemStack = new Item(Material.valueOf(config.getItemType().toUpperCase()))
                    .setDisplayName(this.replace(config.getItemName(), service, serviceGroup)).addLoreArray(lore).build();
            inventory.addItem(itemStack);
            serviceMap.put(this.getSlot(itemStack, inventory), service);
        }
        this.services.put(player.getUniqueId(), serviceMap);
        return inventory;
    }

    public int getSlot(ItemStack itemStack, Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i).equals(itemStack)) {
                return i;
            }
        }
        return 0;
    }

    public String replace(String input, Service service, ServiceGroup serviceGroup) {
        try {
            if (service != null) {
                ServiceInfo serviceInfo = CloudAPI.getInstance().getNetwork().getServiceInfo(service.getName());
                input = input.replace("%service%", service.getName());
                input = input.replace("%uuid%", service.getUniqueId().toString());
                input = input.replace("%port%", "" + service.getPort());
                input = input.replace("%id%", "" + service.getServiceID());
                input = input.replace("%state%", service.getServiceState().getColor() + service.getServiceState().name());
                input = input.replace("%motd%", serviceInfo.getMotd());
                input = input.replace("%max%", serviceInfo.getMaxPlayers() + "");
                input = input.replace("%online%", serviceInfo.getOnlinePlayers().size() + "");

            }
            if (serviceGroup != null) {
                GroupInfo groupInfo = CloudAPI.getInstance().getNetwork().getGroupInfo(serviceGroup.getName());
                input = input.replace("%group%", groupInfo.getName());
                input = input.replace("%template%", serviceGroup.getTemplate());
                input = input.replace("%type%", serviceGroup.getServiceType().name());
                input = input.replace("%newServer%", "" + serviceGroup.getNewServerPercent());
                input = input.replace("%online_services%", groupInfo.getOnlineServices().size() + "");
                input = input.replace("%online_players%", groupInfo.getOnlinePlayers().size() + "");
            }
            input = input.replace("%prefix%", CloudAPI.getInstance().getPrefix());

        } catch (NullPointerException e) {}
        return input;
    }
}
