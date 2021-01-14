package de.lystx.cloudapi.bukkit.manager.npc.impl;

import de.lystx.cloudapi.CloudAPI;
import de.lystx.cloudapi.bukkit.CloudServer;
import de.lystx.cloudapi.bukkit.command.ServiceCommand;
import de.lystx.cloudapi.bukkit.events.CloudServerNPCInteractEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

public class PacketReader {

    private Player player;
    private Channel channel;

    public PacketReader(Player player) {
        this.player = player;
        this.channel = (((CraftPlayer)player).getHandle()).playerConnection.networkManager.channel;
    }

    public void inject() {
        this.channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Packet<?>>() {
            protected void decode(ChannelHandlerContext arg0, Packet<?> packet, List<Object> arg2) throws Exception {
                arg2.add(packet);
                PacketReader.this.readPacket(packet);
            }
        });
    }

    public void uninject() {
        if (this.channel.pipeline().get("PacketInjector") != null) {
            this.channel.pipeline().remove("PacketInjector");
        }
    }

    public void readPacket(Packet<?> packet) {
        if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
            int id = (Integer) getValue(packet, "a");
            if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT")) {
                if (NPC.npcByID.get(id) == null) {
                    return;
                }
                CloudServerNPCInteractEvent spigotCloudEventNPCInteract = new CloudServerNPCInteractEvent(this.player, NPC.npcByID.get(id), true);
                Bukkit.getPluginManager().callEvent(spigotCloudEventNPCInteract);
            } else if (getValue(packet, "action").toString().equalsIgnoreCase("ATTACK")) {
                NPC npc = NPC.npcByID.get(id);
                if (npc == null) {
                    return;
                }
                if (ServiceCommand.deleters.contains(player.getUniqueId())) {
                    NPC getSafe = CloudServer.getInstance().getNpcManager().getNPC(npc.getLocation());
                    if (getSafe != null) {
                        String group = CloudServer.getInstance().getNpcManager().getGroupNPCS().get(getSafe);
                        CloudServer.getInstance().getNpcManager().deleteNPC(getSafe);
                        ServiceCommand.deleters.remove(player.getUniqueId());
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§7Removed NPC for group §b" + group + "§8!");
                    } else {
                        player.sendMessage(CloudAPI.getInstance().getPrefix() + "§cThe NPC couldn't be found!");
                    }
                    return;
                }
                CloudServerNPCInteractEvent spigotCloudEventNPCInteract = new CloudServerNPCInteractEvent(this.player, npc, false);
                Bukkit.getPluginManager().callEvent(spigotCloudEventNPCInteract);
            }
        }
    }

    public Object getValue(Object obj, String name) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception exception) {
            return null;
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public Player getPlayer() {
        return player;
    }
}
