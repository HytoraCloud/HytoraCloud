package de.lystx.hytoracloud.module.serverselector.spigot.manager.npc.impl;

import de.lystx.hytoracloud.driver.commons.packets.both.other.PacketInformation;
import de.lystx.hytoracloud.driver.utils.utillity.CloudMap;
import de.lystx.hytoracloud.module.serverselector.spigot.SpigotSelector;
import de.lystx.hytoracloud.module.serverselector.spigot.event.CloudServerNPCInteractEvent;
import de.lystx.hytoracloud.driver.CloudDriver;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class PacketReader {

    private final Player player;
    private Channel channel;

    public PacketReader(Player player) {
        this.player = player;
        try {
            Method getHandle = player.getClass().getMethod("getHandle", (Class<?>[]) null);
            Object entityPlayer = getHandle.invoke(player);
            Object connection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);
            Object networkManager = connection.getClass().getDeclaredField("networkManager").get(connection);
            this.channel = (Channel) networkManager.getClass().getDeclaredField("channel").get(networkManager);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {}
    }

    /**
     * Injects a {@link MessageToMessageDecoder}
     * from Netty into the player's connection
     * to check if clicking an {@link NPC}
     */
    public void inject() {
        try {
            this.channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Object>() {
                protected void decode(ChannelHandlerContext arg0, Object packet, List<Object> arg2) throws Exception {
                    arg2.add(packet);
                    PacketReader.this.readPacket(packet);
                }
            });
        } catch (NoSuchElementException ignored){}
    }

    /**
     * Removes the pipeline from the player
     */
    public void uninject() {
        if (this.channel.pipeline().get("PacketInjector") != null) {
            this.channel.pipeline().remove("PacketInjector");
        }
    }

    public void readPacket(Object packet) {
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
                if (CloudDriver.getInstance().getImplementedData().getList("uuidList", UUID.class).contains(player.getUniqueId())) {
                    NPC getSafe = SpigotSelector.getInstance().getNpcManager().getNPC(npc.getLocation());
                    if (getSafe != null) {
                        String group = SpigotSelector.getInstance().getNpcManager().getGroupNPCS().get(getSafe);



                        PacketInformation packetInformation = new PacketInformation("PacketInDeleteNPC", new CloudMap<String, Object>()
                                .append("key", SpigotSelector.getInstance().getNpcManager().getKey(getSafe)));

                        CloudDriver.getInstance().sendPacket(packetInformation);

                        List<UUID> uuidList = CloudDriver.getInstance().getImplementedData().getList("uuidList", UUID.class);
                        uuidList.remove(player.getUniqueId());
                        CloudDriver.getInstance().getImplementedData().put("uuidList", uuidList);
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Removed NPC for group §b" + group + "§8!");
                    } else {
                        player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe NPC couldn't be found!");
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
}