package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.listener;

import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.impl.NPC;
import de.lystx.hytoracloud.bridge.spigot.bukkit.utils.BukkitItem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.event.handle.IEventHandler;
import de.lystx.hytoracloud.driver.player.ICloudPlayer;
import de.lystx.hytoracloud.driver.player.inventory.Item;
import de.lystx.hytoracloud.driver.serverselector.npc.NPCConfig;
import de.lystx.hytoracloud.driver.serverselector.npc.NPCMeta;
import de.lystx.hytoracloud.driver.event.events.player.other.DriverEventPlayerNPC;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.utils.other.CloudMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class PlayerNPCListener implements IEventHandler<DriverEventPlayerNPC> {

    private final Map<UUID, Map<Integer, IService>> services;

    public PlayerNPCListener() {
        this.services = new HashMap<>();
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
            if (event.getCurrentItem().getType().equals(Material.valueOf(ServerSelector.getInstance().getNpcManager().getNpcConfig().getServerItem().getMaterial()))) {
                ICloudPlayer cachedPlayer = CloudDriver.getInstance().getPlayerManager().getCachedObject(player.getName());
                if (cachedPlayer == null) {
                    return;
                }
                IService service = serviceMap.get(event.getSlot());
                if (service.getName().equalsIgnoreCase(CloudDriver.getInstance().getServiceManager().getThisService().getName())) {
                    player.sendMessage(CloudDriver.getInstance().getConfigManager().getNetworkConfig().getMessageConfig().getAlreadyConnected().replace("&", "§").replace("%prefix%", CloudDriver.getInstance().getPrefix()));
                    return;
                }
                if (!ServerSelector.getInstance().getNpcManager().getNpcConfig().getConnectingMessage().trim().isEmpty()) {
                    player.sendMessage(this.replace(ServerSelector.getInstance().getNpcManager().getNpcConfig().getConnectingMessage(), service, service.getGroup()));
                }
                cachedPlayer.connect(service);
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
        NPCConfig config = ServerSelector.getInstance().getNpcManager().getNpcConfig();
        IServiceGroup serviceGroup = CloudDriver.getInstance().getServiceManager().getServiceGroup(group);

        if (serviceGroup == null) {
            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThere was an error!");
            return null;
        }

        Inventory inventory = Bukkit.createInventory(player, (config.getInventoryRows() * 9), this.replace(config.getInventoryTitle(), null, serviceGroup));
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

        for (Item item : config.getItems()) {

            List<String> lore = item.getLore();
            for (String s : lore) {
                lore.set(lore.indexOf(s), this.replace(s, null, serviceGroup));
            }
            item.display(this.replace(item.getDisplayName(), null, serviceGroup));
            item.lore(lore);

            inventory.setItem(item.getPreInventorySlot(), BukkitItem.fromCloudItem(item));
        }

        for (IService service : CloudDriver.getInstance().getServiceManager().getCachedObjects(serviceGroup)) {
            try {
                List<String> strings = new LinkedList<>();

                Item serverItem = config.getServerItem();

                List<String> lore = serverItem.getLore();
                for (String s : lore) {
                    lore.set(lore.indexOf(s), this.replace(s, service, serviceGroup));
                }
                serverItem.display(this.replace(serverItem.getDisplayName(), service, serviceGroup));
                serverItem.lore(lore);

                ItemStack itemStack = BukkitItem.fromCloudItem(serverItem);
                inventory.addItem(itemStack);

                serviceMap.put(this.getSlot(itemStack, inventory), service);
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
                input = input.replace("%template%", IServiceGroup.getCurrentTemplate().getName());
                input = input.replace("%type%", IServiceGroup.getEnvironment().name());
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

    @Override
    public void handle(DriverEventPlayerNPC event) {
        NPCMeta npcMeta = event.getNpcMeta();
        ICloudPlayer player = event.getPlayer();
        Map<NPC, NPCMeta> npcs = ServerSelector.getInstance().getNpcManager().getNpcs();

        if (npcs instanceof CloudMap) {
            CloudMap<NPC, NPCMeta> cloudMap = (CloudMap<NPC, NPCMeta>) npcs;
            NPCMeta safeGet = cloudMap.values().stream().filter(meta -> meta.getGroup().equalsIgnoreCase(npcMeta.getGroup()) && meta.getName().equalsIgnoreCase(npcMeta.getName())).findFirst().orElse(null);
            if (safeGet != null) {
                NPC npc = cloudMap.getKey(safeGet);
                String group = ServerSelector.getInstance().getNpcManager().getNpcs().get(npc).getGroup();
                if (group == null) {
                    player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cCan't handle NPC because group was not found!");
                    return;
                }
                CloudDriver.getInstance().getExecutorService().execute(() -> Bukkit.getPlayer(player.getName()).openInventory(this.getInventory(Bukkit.getPlayer(player.getName()), group)));
            }
        }

    }
}
