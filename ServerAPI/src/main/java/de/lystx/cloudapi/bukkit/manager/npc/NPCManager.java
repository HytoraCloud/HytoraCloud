package de.lystx.cloudapi.bukkit.manager.npc;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.manager.npc.impl.Hologram;
import de.lystx.cloudapi.bukkit.manager.npc.impl.NPC;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketPlayInCreateNPC;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketPlayInRemoveNPC;
import de.lystx.cloudsystem.library.elements.other.Document;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class NPCManager {

    private final Map<NPC, String> npcs;
    private final Map<NPC, String> groupNPCS;
    private final Map<Integer, Hologram> holograms;
    private Document document;

    public NPCManager() {
        this.npcs = new HashMap<>();
        this.groupNPCS = new HashMap<>();
        this.holograms = new HashMap<>();
        this.document = new Document();
    }

    public void createNPC(Location location, String name, String group, String skin) {
        Document document = new Document()
                .append("location", new Document()
                        .append("x", location.getX())
                        .append("y", location.getY())
                        .append("z", location.getZ())
                        .append("yaw", location.getYaw())
                        .append("pitch", location.getPitch())
                        .append("world", location.getWorld().getName()))
                .append("name", name)
                .append("skin", skin)
                .append("group", group);
        CloudAPI.getInstance().sendPacket(new PacketPlayInCreateNPC(name + "_" + group + UUID.randomUUID(), document.toString()));
        Bukkit.getOnlinePlayers().forEach(player -> this.updateNPCS(this.document, player));
    }

    public void deleteNPC(NPC npc) {
        CloudAPI.getInstance().sendPacket(new PacketPlayInRemoveNPC(this.getKey(npc)));
        Hologram hologram = this.holograms.get(npc.getEntityID());
        hologram.delete();
    }

    public String getKey(NPC npc) {
        return this.npcs.get(npc);
    }

    public NPC getNPC(Location location) {
        for (NPC npc : this.npcs.keySet()) {
            Location loc = npc.getLocation();
            if (loc.getBlockX() == location.getBlockX() && loc.getBlockY() == location.getBlockY() && loc.getBlockZ() == location.getBlockZ()) {
                return npc;
            }
        }
        return null;
    }

    public void updateNPCS(Document document, Player player) {
        if (!CloudAPI.getInstance().getService().getServiceGroup().isLobby()) {
            return;
        }
        for (NPC npc : this.npcs.keySet()) {
            npc.destroy();
        }
        for (Hologram value : this.holograms.values()) {
            value.delete();
        }
        this.groupNPCS.clear();
        this.npcs.clear();
        this.holograms.clear();
        this.document = document;
        for (String key : document.keys()) {
            Document doc = document.getDocument(key);
            Document loc = doc.getDocument("location");
            if (!loc.getString("world").equalsIgnoreCase(player.getWorld().getName())) {
                return;
            }
            Location location = new Location(Bukkit.getWorld(loc.getString("world")), loc.getDouble("x"), loc.getDouble("y"), loc.getDouble("z"),
                    loc.getFloat("yaw"), loc.getFloat("pitch"));
            String name = doc.getString("name");
            String skin = doc.getString("skin");
            String group = doc.getString("group");
            Hologram hologram = new Hologram(location.clone().subtract(0, 0.2, 0));
            if (name.startsWith("[i:") && name.endsWith("]")) {
                name = name.replace("[i:", "");
                name = name.replace("]", "");
                Material material = Material.valueOf(name);
                hologram.append(new ItemStack(material));
            } else {
                hologram.append(name);
            }
            name = "";
            hologram.send(player);
            NPC npc = new NPC(name, location);
            this.holograms.put(npc.getEntityID(), hologram);
            npc.setSkin(skin);
            this.npcs.put(npc, key);
            this.groupNPCS.put(npc, group);
            npc.spawn(player);
        }
    }
}
