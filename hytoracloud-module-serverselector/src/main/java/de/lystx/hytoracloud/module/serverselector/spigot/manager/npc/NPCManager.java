package de.lystx.hytoracloud.module.serverselector.spigot.manager.npc;

import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.elements.other.JsonBuilder;
import de.lystx.hytoracloud.driver.elements.packets.both.other.PacketInformation;
import de.lystx.hytoracloud.driver.service.util.utillity.CloudMap;
import de.lystx.hytoracloud.module.serverselector.cloud.manager.npc.NPCConfig;
import de.lystx.hytoracloud.module.serverselector.spigot.manager.npc.impl.NPC;
import io.vson.elements.object.VsonObject;
import io.vson.enums.VsonSettings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private JsonBuilder jsonBuilder;
    private NPCConfig npcConfig;

    public NPCManager() {
        this.npcs = new HashMap<>();
        this.groupNPCS = new HashMap<>();
        this.jsonBuilder = new JsonBuilder();
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

        PacketInformation packetInformation = new PacketInformation("PacketInCreateNPC", new CloudMap<String, Object>()
                .append("key", name + "_" + group + UUID.randomUUID())
                .append("vsonObject", VsonObject.encode(document)));
        CloudDriver.getInstance().sendPacket(packetInformation);
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
        Bukkit.getOnlinePlayers().forEach(player -> this.updateNPCS(jsonBuilder, player, false));
    }

    /**
     * Updates all NPCs for a specific Player
     * with a custom {@link NPCConfig}
     *
     * @param jsonBuilder > NPCConfig parsed as {@link VsonObject}
     * @param player > Player to update NPCs for
     * @param join > If update is on join
     */
    public void updateNPCS(JsonBuilder jsonBuilder, Player player, boolean join) {
        this.jsonBuilder = jsonBuilder;
        if (!CloudDriver.getInstance().getThisService().getServiceGroup().isLobby()) {
            return;
        }
        if (CloudDriver.getInstance().getBukkit().isNewVersion()) {
            return;
        }
        if (join) {
            this.npcs.keySet().forEach(npc -> npc.destroy(player));
        }
        jsonBuilder.keys().forEach(key -> {
            JsonBuilder doc = jsonBuilder.getJson(key);
            try {
                VsonObject loc = new VsonObject(doc.getJson("location").toString());
                if (loc.isEmpty()) {
                    return;
                }
                if (!loc.getString("world").equalsIgnoreCase(player.getWorld().getName())) {
                    return;
                }
                Location location = new Location(Bukkit.getWorld(loc.getString("world")), loc.getDouble("x"), loc.getDouble("y"), loc.getDouble("z"),
                        loc.getFloat("yaw"), loc.getFloat("pitch"));

                if (this.getNPC(location) == null || join) {
                    String name = doc.getString("name");
                    String skin = doc.getString("skin");
                    String group = doc.getString("group");
                    NPC npc = new NPC(name, location);
                    this.npcs.put(npc, key);
                    this.groupNPCS.put(npc, group);
                    npc.setSkin(skin);
                    npc.spawn(player);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
