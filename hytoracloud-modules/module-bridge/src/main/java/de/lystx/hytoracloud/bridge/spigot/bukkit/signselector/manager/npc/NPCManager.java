package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc;

import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.impl.NPC;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc.NPCConfig;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc.NPCMeta;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public class NPCManager {

    /**
     * All the cached {@link NPC}s
     */
    private final Map<NPC, NPCMeta> npcs;

    /**
     * All cached {@link NPCMeta}s
     */
    private List<NPCMeta> npcMetas;

    /**
     * The npc config
     */
    private NPCConfig npcConfig;

    public NPCManager() {
        this.npcs = new HashMap<>();
    }

    /**
     * Returns NPC by Bukkit Location
     *
     * @param location the location
     * @return npc or null
     */
    public NPC getNPC(Location location) {
        return this.npcs.keySet().stream().filter(npc -> {
            Location loc = npc.getLocation();
            return loc.getBlockX() == location.getBlockX() && loc.getBlockY() == location.getBlockY() && loc.getBlockZ() == location.getBlockZ();
        }).findFirst().orElse(null);
    }

    /**
     * Updates NPCs for all Players
     */
    public void updateNPCS() {
        Bukkit.getOnlinePlayers().forEach(player -> this.updateNPCS(player, false));
    }

    /**
     * Updates all NPCs for a specific Player
     * with a custom {@link NPCConfig}
     *
     * @param player > Player to update NPCs for
     * @param join > If update is on join
     */
    public void updateNPCS(Player player, boolean join) {
        if (CloudDriver.getInstance().getServiceManager().getThisService() == null) {
            return;
        }
        if (!CloudDriver.getInstance().getServiceManager().getThisService().getGroup().isLobby()) {
            return;
        }
        if (CloudDriver.getInstance().getBukkit().isNewVersion()) {
            return;
        }


        if (join) {
            this.npcs.keySet().forEach(npc -> npc.destroy(player));
        }

        for (NPCMeta npcMeta : this.npcMetas) {

            if (!npcMeta.getLocation().get("world").equals(player.getWorld().getName())) {
                return;
            }

            Location location = Location.deserialize(npcMeta.getLocation());
            if (this.getNPC(location) == null || join) {

                NPC npc = new NPC(npcMeta.getName(), location);
                npc.setSkin(npcMeta.getSkin());
                npc.spawn(player);

                this.npcs.put(npc, npcMeta);
            }
        }

    }
}
