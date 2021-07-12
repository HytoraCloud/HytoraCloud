package de.lystx.hytoracloud.module.serverselector.spigot.listener;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.utillity.PropertyObject;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.npc.NPCConfig;
import de.lystx.hytoracloud.module.serverselector.spigot.SpigotSelector;
import de.lystx.hytoracloud.module.serverselector.spigot.event.CloudServerNPCInteractEvent;
import de.lystx.hytoracloud.bridge.bukkit.utils.BukkitItem;
import de.lystx.hytoracloud.driver.commons.service.IService;
import de.lystx.hytoracloud.driver.commons.service.IServiceGroup;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.module.serverselector.spigot.manager.npc.impl.NPC;
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

@Getter
public class NPCListener implements Listener {

    private final Map<UUID, Map<Integer, IService>> services;

    public NPCListener() {
        this.services = new HashMap<>();
    }


    @EventHandler
    public void onInteract(CloudServerNPCInteractEvent event) {
        NPC npcV18R3V18R3 = event.getNpc();
        Player player = event.getPlayer();
        String group = SpigotSelector.getInstance().getNpcManager().getGroupNPCS().get(npcV18R3V18R3);
        if (group == null) {
            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cCan't handle NPC because group was not found!");
            return;
        }
        CloudDriver.getInstance().execute(() -> player.openInventory(this.getInventory(player, group)));
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
        Map<Integer, IService> serviceMap = this.services.getOrDefault(player.getUniqueId(), new HashMap<>());
        if (player.getOpenInventory() != null && !serviceMap.isEmpty()) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType().equals(Material.valueOf(SpigotSelector.getInstance().getNpcManager().getNpcConfig().getItemType()))) {

                ICloudPlayer ICloudPlayer = CloudDriver.getInstance().getCloudPlayerManager().getCachedPlayer(player.getName());
                if (ICloudPlayer == null) {
                    player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cCouldn't find you in global CloudPlayers!");
                    return;
                }
                IService IService = serviceMap.get(event.getSlot());
                if (!SpigotSelector.getInstance().getNpcManager().getNpcConfig().getConnectingMessage().trim().isEmpty()) {
                    player.sendMessage(this.replace(SpigotSelector.getInstance().getNpcManager().getNpcConfig().getConnectingMessage(), IService, IService.getGroup()));
                }
                ICloudPlayer.connect(IService);
            }
        }
    }


    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        this.services.remove(event.getPlayer().getUniqueId());
    }

    /**
     * Opens the Inventory for the given group
     * @param player > Player to open to
     * @param group > ServerGroup
     * @return > Group Inventory
     */
    public Inventory getInventory(Player player, String group) {
        Map<Integer, IService> serviceMap = new HashMap<>();
        NPCConfig config = SpigotSelector.getInstance().getNpcManager().getNpcConfig();
        IServiceGroup IServiceGroup = CloudDriver.getInstance().getServiceManager().getServiceGroup(group);
        if (IServiceGroup == null) {
            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThere was an error!");
            return null;
        }
        Inventory inventory = Bukkit.createInventory(player, (config.getInventoryRows() * 9), this.replace(config.getInventoryTitle(), null, IServiceGroup));
        if (config.isCorners()) {
            ItemStack glass = new BukkitItem(Material.STAINED_GLASS_PANE, (short) 7).setNoName().build();

            for (int i = 0; i < 8; i++) {
                inventory.setItem(i, glass);
            }

            for (int s = 8; s < (inventory.getSize() - 9); s += 9) {
                int lastSlot = s + 1;
                inventory.setItem(s, glass);
                inventory.setItem(lastSlot, glass);

            }
            for (int lr = (inventory.getSize() - 9); lr < inventory.getSize(); lr++) {
                inventory.setItem(lr, glass);
            }
        }

        for (PropertyObject document : config.getItems()) {
            if (document.isEmpty()) {
                continue;
            }
            List<String> lore = document.get("lore", List.class);
            for (String s : lore) {
                lore.set(lore.indexOf(s), this.replace(s, null, IServiceGroup));
            }

            int slot = document.getInteger("slot");
            inventory.setItem(slot, new BukkitItem(Material.valueOf(document.getString("type")))
                    .setDisplayName(this.replace(document.getString("name"), null, IServiceGroup))
                    .addLores(lore)
                    .build());
        }

        for (IService IService : CloudDriver.getInstance().getServiceManager().getServices(IServiceGroup)) {
            try {
                List<String> strings = new LinkedList<>();

                for (String s : config.getLore()) {
                    strings.add(this.replace(s, IService, IServiceGroup));
                }

                ItemStack itemStack = new BukkitItem(Material.valueOf(config.getItemType().toUpperCase()))
                        .setDisplayName(this.replace(config.getItemName(), IService, IServiceGroup))
                        .addLores(strings)
                        .build();
                inventory.addItem(itemStack);

                serviceMap.put(this.getSlot(itemStack, inventory), IService);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.services.put(player.getUniqueId(), serviceMap);
        return inventory;
    }

    /**
     * Returns the slot by an ItemStack
     * in an given Inventory
     * @param itemStack > Index to stop
     * @param inventory > Inv to scan
     * @return slot
     */
    public int getSlot(ItemStack itemStack, Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i).equals(itemStack)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Returns String with placeHolders
     * @param input
     * @param IService
     * @param IServiceGroup
     * @return
     */
    public String replace(String input, IService IService, IServiceGroup IServiceGroup) {
        try {
            if (IService != null) {
                input = input.replace("%service%", IService.getName());
                input = input.replace("%uuid%", IService.getUniqueId().toString());
                input = input.replace("%port%", "" + IService.getPort());
                input = input.replace("%id%", "" + IService.getId());
                input = input.replace("%state%", IService.getState().getColor() + IService.getState().name());

                String motd = IService.getMotd() == null ? "no_motd" : IService.getMotd();
                String maxPlayers = String.valueOf(IService.getMaxPlayers());
                String online = String.valueOf(IService.getPlayers().size());
                input = input.replace("%motd%", motd);
                input = input.replace("%max%", maxPlayers);
                input = input.replace("%online%", online);

            }
            if (IServiceGroup != null) {
                IServiceGroup group = CloudDriver.getInstance().getServiceManager().getServiceGroup(IServiceGroup.getName());
                input = input.replace("%group%", group.getName());
                input = input.replace("%template%", IServiceGroup.getTemplate().getName());
                input = input.replace("%type%", IServiceGroup.getType().name());
                input = input.replace("%newServer%", "" + IServiceGroup.getNewServerPercent());
                input = input.replace("%online_services%", group.getServices().size() + "");
                input = input.replace("%online_players%", group.getPlayers().size() + "");
            }
            input = input.replace("%prefix%", CloudDriver.getInstance().getPrefix());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return input;
    }
}
