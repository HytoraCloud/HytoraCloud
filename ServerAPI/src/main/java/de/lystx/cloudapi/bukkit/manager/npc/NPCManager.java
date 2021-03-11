package de.lystx.cloudapi.bukkit.manager.npc;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.manager.npc.impl.NPC;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketInCreateNPC;
import de.lystx.cloudsystem.library.elements.packets.in.serverselector.PacketInDeleteNPC;
import de.lystx.cloudsystem.library.service.serverselector.npc.NPCConfig;
import io.vson.elements.object.VsonObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class NPCManager {

    private final Map<NPC, String> npcs;
    private final Map<NPC, String> groupNPCS;
    private VsonObject document;
    private NPCConfig npcConfig;

    public NPCManager() {
        this.npcs = new HashMap<>();
        this.groupNPCS = new HashMap<>();
        this.document = new VsonObject();
    }

    public void createNPC(Location location, String name, String group, String skin) {
        VsonObject document = new VsonObject()
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

    public void deleteNPC(NPC npcV18R3V18R3) {
        CloudAPI.getInstance().sendPacket(new PacketInDeleteNPC(this.getKey(npcV18R3V18R3)));
    }

    public String getKey(NPC npcV18R3V18R3) {
        return this.npcs.get(npcV18R3V18R3);
    }

    public NPC getNPC(Location location) {
        for (NPC npcV18R3V18R3 : this.npcs.keySet()) {
            Location loc = npcV18R3V18R3.getLocation();
            if (loc.getBlockX() == location.getBlockX() && loc.getBlockY() == location.getBlockY() && loc.getBlockZ() == location.getBlockZ()) {
                return npcV18R3V18R3;
            }
        }
        return null;
    }


    public void updateNPCS() {
        Bukkit.getOnlinePlayers().forEach(player -> this.updateNPCS(document, player, false));
    }

    public void updateNPCS(VsonObject document, Player player, boolean join) {
        this.document = document;
        if (!CloudAPI.getInstance().getService().getServiceGroup().isLobby()) {
            return;
        }
        if (CloudServer.getInstance().isNewVersion()) {
            return;
        }
        if (!join) {
            for (NPC npc : this.npcs.keySet()) {
                npc.destroy(player);
            }
        }
        for (String key : document.keys()) {
            VsonObject doc = document.getVson(key);
            VsonObject loc = doc.getVson("location", new VsonObject());
            if (loc.isEmpty()) {
                continue;
            }
            if (!loc.getString("world").equalsIgnoreCase(player.getWorld().getName())) {
                continue;
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
        }
    }
}
