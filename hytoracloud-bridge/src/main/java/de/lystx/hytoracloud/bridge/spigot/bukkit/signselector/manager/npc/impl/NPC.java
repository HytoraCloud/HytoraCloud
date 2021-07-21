package de.lystx.hytoracloud.bridge.spigot.bukkit.signselector.manager.npc.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.utils.Reflections;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;


@Getter @Setter
public class NPC implements Serializable {

    private static final long serialVersionUID = -1840140405908439982L;

    /**
     * The entity id of this npc
     */
    private final int entityID;

    /**
     * The gameprofile
     */
    private final GameProfile gameprofile;

    /**
     * The location of this npc
     */
    private Location location;

    /**
     * The health of this npc
     */
    private float health;

    /**
     * The helmet of this npc
     */
    private org.bukkit.inventory.ItemStack helmet;

    /**
     * The item in hand of this npc
     */
    private org.bukkit.inventory.ItemStack handHeld;

    /**
     * The chestplate of this npc
     */
    private org.bukkit.inventory.ItemStack chestplate;

    /**
     * The leggins of this npc
     */
    private org.bukkit.inventory.ItemStack leggins;

    /**
     * The boots of this npc
     */
    private org.bukkit.inventory.ItemStack boots;


    /**
     * Cached npcs by their entity ID
     */
    public static final Map<Integer, NPC> npcByID = new HashMap<>();


    /**
     * The skin fetcher for the skin method
     */
    private static final SkinFetcher skinFetcher = new SkinFetcher();


    /**
     * Constructs an {@link NPC} with a displayname
     * and a given location
     *
     * @param name the name
     * @param location the location
     */
    public NPC(String name, Location location) {
        this(name, (int) Math.ceil(Math.random() * 1000) + 2000, UUID.randomUUID(), location.clone());
    }

    /**
     * Constructs an {@link NPC}
     *
     * @param name the name
     * @param entityID the entity id
     * @param uuid the uuid
     * @param location the location
     */
    public NPC(String name, Integer entityID, UUID uuid, Location location) {
        this.entityID = entityID;
        this.gameprofile = new GameProfile(uuid, name);
        this.location = location;
        this.health = 20F;

        this.helmet = null;
        this.chestplate = null;
        this.leggins = null;
        this.boots = null;
        this.handHeld = null;
    }

    /**
     * Sets the health of this npc
     *
     * @param health the health
     * @return current NPC
     */
    public NPC setHealth(Float health) {
        this.health = health;
        return this;
    }

    /**
     * Sets the skin of this npc
     *
     * @param name the skin name
     * @return current npc
     */
    public NPC setSkin(String name) {
        Gson gson = new Gson();
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;


        String json = "";
        try {
            Scanner scanner = new Scanner(new URL(url).openStream());
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                while (line.startsWith(" ")) {
                    line = line.substring(1);
                }
                json = json + line;
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String uuid = gson.fromJson(json, JsonObject.class).get("id").getAsString();
        String value = skinFetcher.getSkinValue(uuid);
        String signatur = skinFetcher.getSkinSignature(uuid);
        gameprofile.getProperties().put("textures", new Property("textures", value, signatur));
        return this;
    }

    /**
     * Plays an animation for this npc
     *
     * @param animation the animation id
     * @return current npc
     */
    public NPC playAnimation(int animation) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        Reflections.setValue(packet, "a", entityID);
        Reflections.setValue(packet, "b", (byte) animation);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Reflections.sendPacket(onlinePlayer, packet);
        }
        return this;
    }

    /**
     * Plays a status for this npc
     *
     * @param status the status
     * @return current npc
     */
    public NPC playStatus(int status) {
        PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus();
        Reflections.setValue(packet, "a", entityID);
        Reflections.setValue(packet, "b", (byte) status);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Reflections.sendPacket(onlinePlayer, packet);
        }
        return this;
    }

    /**
     * Equips the npc with an item
     *
     * @param slot the slot
     * @param itemstack the item
     * @return current npc
     */
    public NPC setItem(Integer slot, org.bukkit.inventory.ItemStack itemstack) {
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
        Reflections.setValue(packet, "a", entityID);
        Reflections.setValue(packet, "b", slot);
        Reflections.setValue(packet, "c", itemstack);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Reflections.sendPacket(onlinePlayer, packet);
        }
        return this;
    }

    /**
     * Sets the helmet of this npc
     *
     * @param itemstack the helmet
     * @return current npc
     */
    public NPC setHelmet(org.bukkit.inventory.ItemStack itemstack) {
        this.setItem(4, itemstack);
        this.helmet = itemstack;
        this.updateItems(this.handHeld, this.boots, this.leggins, this.chestplate, this.helmet);
        return this;
    }

    /**
     * Sets the chestplate of this npc
     *
     * @param itemstack the chestplate
     * @return current npc
     */
    public NPC setChestplate(org.bukkit.inventory.ItemStack itemstack) {
        this.setItem(3, itemstack);
        this.chestplate = itemstack;
        this.updateItems(this.handHeld, this.boots, this.leggins, this.chestplate, this.helmet);
        return this;
    }

    /**
     * Sets the leggings of this npc
     *
     * @param itemstack the leggings
     * @return current npc
     */
    public NPC setLeggings(org.bukkit.inventory.ItemStack itemstack) {
        this.setItem(2, itemstack);
        this.leggins = itemstack;
        this.updateItems(this.handHeld, this.boots, this.leggins, this.chestplate, this.helmet);
        return this;
    }

    /**
     * Sets the boots of this npc
     *
     * @param itemstack the boots
     * @return current npc
     */
    public NPC setBoots(org.bukkit.inventory.ItemStack itemstack) {
        this.setItem(1, itemstack);
        this.boots = itemstack;
        this.updateItems(this.handHeld, this.boots, this.leggins, this.chestplate, this.helmet);
        return this;
    }

    /**
     * Sets the item in hand of this npc
     *
     * @param itemstack the item in hand
     * @return current npc
     */
    public NPC setItemInHand(org.bukkit.inventory.ItemStack itemstack) {
        this.setItem(0, itemstack);
        this.handHeld = itemstack;
        this.updateItems(this.handHeld, this.boots, this.leggins, this.chestplate, this.helmet);
        return this;
    }

    /**
     * Spawns the npc for a given player
     *
     * @param player the player
     */
    public void spawn(Player player) {
        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
        Reflections.setValue(packet, "a", entityID);
        Reflections.setValue(packet, "b", gameprofile.getId());

        Reflections.setValue(packet, "c", MathHelper.floor(location.getX() * 32.0D));
        Reflections.setValue(packet, "d", MathHelper.floor(location.getY() * 32.0D));
        Reflections.setValue(packet, "e", MathHelper.floor(location.getZ() * 32.0D));
        Reflections.setValue(packet, "f", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        Reflections.setValue(packet, "g", (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));
        Reflections.setValue(packet, "h", 0);
        DataWatcher w = new DataWatcher(null);
        w.a(6, health);


        //Adding to tablist
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.PlayerInfoData data = packetPlayOutPlayerInfo.new PlayerInfoData(gameprofile, 1, EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameprofile.getName())[0]);

        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) Reflections.getValue(packet, "b");
        if (players != null) {
            players.add(data);
            Reflections.setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
            Reflections.setValue(packet, "b", players);

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Reflections.sendPacket(onlinePlayer, packet);
            }
        } else {
            System.out.println("[CloudBridge] Couldn't get field for NPC from Method spawn(Player)!");
        }

        w.a(10, (byte) 127);
        Reflections.setValue(packet, "i", w);
        Reflections.sendPacket(player, packet);
        this.headRotation(location.getYaw(), location.getPitch());
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(this::removeFromTablist, 30L);
        npcByID.put(entityID, this);
    }

    /**
     * Fixes the headroation for this npc
     *
     * @param yaw the yaw to look at
     * @param pitch the pitch to look at
     */
    public void headRotation(float yaw, float pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketPlayOutEntityLook packet = new PacketPlayOutEntityLook(entityID, (byte) ((int) (yaw * 256.0F / 360.0F)), (byte) ((int) (pitch * 256.0F / 360.0F)), true);
            PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
            Reflections.setValue(packetHead, "a", entityID);
            Reflections.setValue(packetHead, "b", (byte) ((int) (yaw * 256.0F / 360.0F)));

            Reflections.sendPacket(player, packet);
            Reflections.sendPacket(packetHead, packet);

            this.location.setYaw(yaw);
            this.location.setPitch(pitch);
        }
    }


    /**
     * Despawns the npc for a given player
     *
     * @param player the player
     * @return current npc
     */
    public NPC destroy(Player player) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityID);
        this.removeFromTablist(player);
        Reflections.sendPacket(player, packet);
        return this;
    }

    /**
     * Removes this npc from tablist
     *
     */
    public void removeFromTablist() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeFromTablist(player);
        }
    }

    /**
     * Removes this npc from tablist
     * only for agiven player
     *
     * @param player the player
     */
    public void removeFromTablist(Player player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameprofile.getName())[0]);

        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) Reflections.getValue(packet, "b");
        if (players != null) {
            players.add(data);
            Reflections.setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
            Reflections.setValue(packet, "b", players);

            Reflections.sendPacket(player, packet);
        } else {
            System.out.println("[NPC] Couldn't get field for NPC from Method removeFromTablist(Player player)!");
        }
    }

    /**
     * Updates all the items for the npc
     * 
     * @param inHand the item in hand
     * @param boots the boots
     * @param leggins the leggings
     * @param chestplate the chestplate
     * @param helmet the helmet
     */
    public void updateItems(org.bukkit.inventory.ItemStack inHand, org.bukkit.inventory.ItemStack boots, org.bukkit.inventory.ItemStack leggins, org.bukkit.inventory.ItemStack chestplate, org.bukkit.inventory.ItemStack helmet) {
       
        this.handHeld = inHand;
        this.boots = boots;
        this.leggins = leggins;
        this.chestplate = chestplate;
        this.helmet = helmet;

        PacketPlayOutEntityEquipment[] packets = { new PacketPlayOutEntityEquipment(getEntityID(), 1, CraftItemStack.asNMSCopy(this.helmet)), new PacketPlayOutEntityEquipment(getEntityID(), 2, CraftItemStack.asNMSCopy(this.chestplate)), new PacketPlayOutEntityEquipment(getEntityID(), 3, CraftItemStack.asNMSCopy(this.leggins)), new PacketPlayOutEntityEquipment(getEntityID(), 4, CraftItemStack.asNMSCopy(this.boots)), new PacketPlayOutEntityEquipment(getEntityID(), 0, CraftItemStack.asNMSCopy(this.handHeld)) };

        for (PacketPlayOutEntityEquipment packet : packets) {

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                Reflections.sendPacket(onlinePlayer, packet);
            }
        }
    }

}
