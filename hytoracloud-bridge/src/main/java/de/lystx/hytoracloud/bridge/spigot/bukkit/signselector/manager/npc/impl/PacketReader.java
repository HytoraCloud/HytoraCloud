package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.impl;

import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.ServerSelector;
import de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.event.CloudServerNPCInteractEvent;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.npc.NPCMeta;
import de.lystx.hytoracloud.driver.commons.packets.in.PacketInNPCDelete;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;


/**
 * Code not originally made by me
 * I don't remember where I got it from
 *
 * I just edited it (It was open source)
 */
public class PacketReader {

    /**
     * The player to inject to
     */
    private final Player player;

    /**
     * The channel of the connection
     */
    private Channel channel;

    public PacketReader(Player player) {
        this.player = player;

        try {
            Method getHandle = player.getClass().getMethod("getHandle", (Class<?>[]) null);
            Object entityPlayer = getHandle.invoke(player);
            Object connection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);
            Object networkManager = connection.getClass().getDeclaredField("networkManager").get(connection);
            this.channel = (Channel) networkManager.getClass().getDeclaredField("channel").get(networkManager);
        } catch (Exception e) {
            //Ignoring
        }
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
        } catch (Exception e){
            //Ignoring this exception
        }
    }

    /**
     * Removes the pipeline from the player
     */
    public void uninject() {
        if (this.channel.pipeline().get("PacketInjector") != null) {
            this.channel.pipeline().remove("PacketInjector");
        }
    }

    /**
     * Reads a packet and then calls the event for
     * the interaction with an {@link NPC}
     *
     * @param packet the packet to read
     */
    public void readPacket(Object packet) {
        try {
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
                    if (CloudDriver.getInstance().getImplementedData().getList("uuidList").contains(player.getUniqueId())) {
                        NPC getSafe = ServerSelector.getInstance().getNpcManager().getNPC(npc.getLocation());
                        if (getSafe != null) {

                            NPCMeta meta = ServerSelector.getInstance().getNpcManager().getNpcs().get(getSafe);

                            CloudDriver.getInstance().sendPacket(new PacketInNPCDelete(meta));

                            List<UUID> uuidList = CloudDriver.getInstance().getImplementedData().getList("uuidList");
                            uuidList.remove(player.getUniqueId());
                            CloudDriver.getInstance().getImplementedData().put("uuidList", uuidList);
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§7Removed NPC for group §b" + meta.getGroup() + "§8!");
                        } else {
                            player.sendMessage(CloudDriver.getInstance().getPrefix() + "§cThe NPC couldn't be found!");
                        }
                        return;
                    }
                    CloudServerNPCInteractEvent spigotCloudEventNPCInteract = new CloudServerNPCInteractEvent(this.player, npc, false);
                    Bukkit.getPluginManager().callEvent(spigotCloudEventNPCInteract);
                }
            }
        } catch (Exception e) {
            //Ignoring
        }
    }

    /**
     * A method to get a field from an object
     *
     * @param obj the object
     * @param name the field name
     * @return field object or null
     */
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