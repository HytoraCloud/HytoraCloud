package de.lystx.cloudapi.bukkit.manager.npc;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.manager.npc.impl.NPC;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketInCreateNPC;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketInDeleteNPC;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCConfig;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;

/**
 * This class is used to manage
 * all the NPC's and its {@link NPCConfig}
 *
 * it updates NPCs for all or just a single {@link Player}
 * on the current Service
 */
@Getter @Setter
public class NPCManager {

    private final Map<NPC, String> npcs;
    private final Map<NPC, String> groupNPCS;
    private VsonObject document;
    private NPCConfig npcConfig;

    public NPCManager() {
        this.npcs = new HashMap<>();
        this.groupNPCS = new HashMap<>();
        this.document = new VsonObject(VsonSettings.CREATE_FILE_IF_NOT_EXIST);
    }

    /**
     * Creates an NPC
     * @param location > Location to spawn
     * @param name > Name of the NPC
     * @param group > Group of the NPC
     * @param skin > Skin of the NPC
     */
    public void createNPC(Location location, String name, String group, String skin) {
        VsonObject document = new VsonObject(VsonSettings.CREATE_FILE_IF_NOT_EXIST)
                .append("location", new VsonObject()
                        .append("x", location.getX())
                        .append("y", location.getY())
                        .append("z", location.getZ())
                        .append("yaw", location.getYaw())
                        .append("pitch", location.getPitch())
                        .append("world", location.getWorld().getName()))
                .append("name", name)
                .append("skin", skin)
                .append("group", group);
        CloudAPI.getInstance().sendPacket(new PacketInCreateNPC(name + "_" + group + UUID.randomUUID(), document));
        this.updateNPCS();
    }

    /**
     * Returns Key of NPC
     * @param npcV18R3V18R3
     * @return
     */
    public String getKey(NPC npcV18R3V18R3) {
        return this.npcs.get(npcV18R3V18R3);
    }

    /**
     * Returns NPC by Bukkit Location
     * @param location
     * @return
     */
    public NPC getNPC(Location location) {
        return this.npcs.keySet().stream().filter(npc -> {
            Location loc = npc.getLocation();
            return loc.getBlockX() == location.getBlockX() && loc.getBlockY() == location.getBlockY() && loc.getBlockZ() == location.getBlockZ();
        }).findFirst().orElse(null);
    }

    /**
     * Updates NPCS for all Players
     */
    public void updateNPCS() {
        Bukkit.getOnlinePlayers().forEach(player -> this.updateNPCS(document, player, false));
    }

    /**
     * Updates all NPCs for a specific Player
     * with a custom {@link NPCConfig}
     *
     * @param document > NPCConfig parsed as {@link VsonObject}
     * @param player > Player to update NPCs for
     * @param join > If update is on join
     */
    public void updateNPCS(VsonObject document, Player player, boolean join) {
        this.document = document;
        if (!CloudAPI.getInstance().getService().getServiceGroup().isLobby()) {
            return;
        }
        if (CloudServer.getInstance().isNewVersion()) {
            return;
        }
        if (!join) {
            this.npcs.keySet().forEach(npc -> npc.destroy(player));
        }
        document.keys().forEach(key -> {
            VsonObject doc = document.getVson(key, VsonSettings.CREATE_FILE_IF_NOT_EXIST);
            VsonObject loc = doc.getVson("location", new VsonObject());
            if (loc.isEmpty()) {
                return;
            }
            if (!loc.getString("world").equalsIgnoreCase(player.getWorld().getName())) {
                return;
            }
            Location location = new Location(Bukkit.getWorld(loc.getString("world")), loc.getDouble("x"), loc.getDouble("y"), loc.getDouble("z"),
                    loc.getFloat("yaw"), loc.getFloat("pitch"));
            String name = doc.getString("name");
            String skin = doc.getString("skin");
            String group = doc.getString("group");
            NPC npc = new NPC(name, location);
            this.npcs.put(npc, key);
            this.groupNPCS.put(npc, group);
            npc.setSkin(skin);
            npc.spawn(player);
        });
    }
}
