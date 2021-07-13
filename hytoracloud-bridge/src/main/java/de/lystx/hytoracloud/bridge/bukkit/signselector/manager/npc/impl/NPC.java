package de.lystx.hytoracloud.bridge.bukkit.signselector.manager.npc.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.lystx.hytoracloud.driver.CloudDriver;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;


//TODO: OPTIMIZE CODE
public class NPC implements Serializable {

    public static Map<Integer, NPC> npcByID = Maps.newConcurrentMap();
    private static final long serialVersionUID = -672963264444438574L;
    private int entityID, sched;
    private Location location;
    private GameProfile gameprofile;
    private Float health = 20F;
    private UUID uuid;
    private final SkinFetcher skinFetcher = new SkinFetcher();

    private org.bukkit.inventory.ItemStack helmet, handHeld, chestplate, leggins, boots;

    public NPC(String name, Location location) {
        entityID = (int) Math.ceil(Math.random() * 1000) + 2000;
        gameprofile = new GameProfile(UUID.randomUUID(), name);
        this.location = location.clone();
        this.uuid = gameprofile.getId();
    }

    public NPC(String name, Integer entityID, UUID uuid, Location location) {
        this.entityID = entityID;
        gameprofile = new GameProfile(uuid, name);
        this.location = location.clone();
        this.uuid = gameprofile.getId();
    }

    public int getEntityID() {
        return entityID;
    }

    public String getName() {
        return gameprofile.getName();
    }

    public Float getHealth() {
        return health;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public NPC setHealth(Float health) {
        this.health = health;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    private String getStringFromURL(String url) {
        String text = "";
        try {
            Scanner scanner = new Scanner(new URL(url).openStream());
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                while (line.startsWith(" ")) {
                    line = line.substring(1);
                }
                text = text + line;
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    public NPC setSkin(String name) {
        Gson gson = new Gson();
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        String json = getStringFromURL(url);
        String uuid = gson.fromJson(json, JsonObject.class).get("id").getAsString();
        String value = skinFetcher.getSkinValue(uuid);
        String signatur = skinFetcher.getSkinSignature(uuid);
        gameprofile.getProperties().put("textures", new Property("textures", value, signatur));
        return this;
    }

    public NPC animation(int animation) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        setValue(packet, "a", entityID);
        setValue(packet, "b", (byte) animation);
        sendPacket(packet);
        return this;
    }

    public NPC status(int status) {
        PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus();
        setValue(packet, "a", entityID);
        setValue(packet, "b", (byte) status);
        sendPacket(packet);
        return this;
    }

    public NPC equip(Integer slot, org.bukkit.inventory.ItemStack itemstack) {
        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment();
        setValue(packet, "a", entityID);
        setValue(packet, "b", slot);
        setValue(packet, "c", itemstack);
        sendPacket(packet);
        return this;
    }

    public NPC sleep(boolean state) {
        if (state) {
            Location bedLocation = new Location(location.getWorld(), 1, 1, 1);
            PacketPlayOutBed packet = new PacketPlayOutBed();
            setValue(packet, "a", entityID);
            setValue(packet, "b", new BlockPosition(bedLocation.getX(), bedLocation.getY(), bedLocation.getZ()));
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.sendBlockChange(bedLocation, Material.BED_BLOCK, (byte) 0);
            }
            sendPacket(packet);
            teleport(location.clone().add(0, 0.3, 0));
        } else {
            animation(2);
            teleport(location.clone().subtract(0, 0.3, 0));
        }
        return this;
    }

    public void spawn() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            spawn(onlinePlayer);
        }
    }

    public NPC setHelmet(org.bukkit.inventory.ItemStack itemstack) {
        equip(4, itemstack);
        this.helmet = itemstack;
        updateItems(this.handHeld, this.boots, this.leggins, this.chestplate, this.helmet);
        return this;
    }

    public NPC setChestplate(org.bukkit.inventory.ItemStack itemstack) {
        equip(3, itemstack);
        this.chestplate = itemstack;
        updateItems(this.handHeld, this.boots, this.leggins, this.chestplate, this.helmet);
        return this;
    }

    public NPC setLeggings(org.bukkit.inventory.ItemStack itemstack) {
        equip(2, itemstack);
        this.leggins = itemstack;
        updateItems(this.handHeld, this.boots, this.leggins, this.chestplate, this.helmet);
        return this;
    }

    public NPC setBoots(org.bukkit.inventory.ItemStack itemstack) {
        equip(1, itemstack);
        this.boots = itemstack;
        updateItems(this.handHeld, this.boots, this.leggins, this.chestplate, this.helmet);
        return this;
    }

    public NPC setItemInHand(org.bukkit.inventory.ItemStack itemstack) {
        equip(0, itemstack);
        this.handHeld = itemstack;
        updateItems(this.handHeld, this.boots, this.leggins, this.chestplate, this.helmet);
        return this;
    }

    public void spawn(Player player) {
        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
        setValue(packet, "a", entityID);
        setValue(packet, "b", gameprofile.getId());
        setValue(packet, "c", getFixLocation(location.getX()));
        setValue(packet, "d", getFixLocation(location.getY()));
        setValue(packet, "e", getFixLocation(location.getZ()));
        setValue(packet, "f", getFixRotation(location.getYaw()));
        setValue(packet, "g", getFixRotation(location.getPitch()));
        setValue(packet, "h", 0);
        DataWatcher w = new DataWatcher(null);
        w.a(6, health);
        addToTablist();
        w.a(10, (byte) 127);
        setValue(packet, "i", w);
        sendPacket(packet, player);
        headRotation(location.getYaw(), location.getPitch());
        CloudDriver.getInstance().getScheduler().scheduleDelayedTask(this::removeFromTablist, 30L);
        npcByID.put(entityID, this);
    }

    public NPC teleport(Location location) {
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport();
        setValue(packet, "a", entityID);
        setValue(packet, "b", getFixLocation(location.getX()));
        setValue(packet, "c", getFixLocation(location.getY()));
        setValue(packet, "d", getFixLocation(location.getZ()));
        setValue(packet, "e", getFixRotation(location.getYaw()));
        setValue(packet, "f", getFixRotation(location.getPitch()));

        sendPacket(packet);
        headRotation(location.getYaw(), location.getPitch());
        this.location = location.clone();
        return this;
    }

    public NPC headRotation(float yaw, float pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            headRotation(yaw, pitch, player);
        }
        return this;
    }

    public NPC headRotation(float yaw, float pitch, Player player) {
        PacketPlayOutEntityLook packet = new PacketPlayOutEntityLook(entityID, getFixRotation(yaw), getFixRotation(pitch), true);
        PacketPlayOutEntityHeadRotation packetHead = new PacketPlayOutEntityHeadRotation();
        setValue(packetHead, "a", entityID);
        setValue(packetHead, "b", getFixRotation(yaw));

        sendPacket(packet, player);
        sendPacket(packetHead, player);

        this.location.setYaw(yaw);
        this.location.setPitch(pitch);
        return this;
    }

    public NPC headRotation(Location location) {
        headRotation(location.getYaw(), location.getPitch());
        return this;
    }


    public NPC destroy() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            destroy(player);
        }
        return this;
    }

    public NPC destroy(Player player) {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] { entityID });
        removeFromTablist();
        sendPacket(packet, player);
        return this;
    }

    public NPC addToTablist() {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameprofile.getName())[0]);

        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
        if (players != null) {
            players.add(data);
            setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
            setValue(packet, "b", players);
            sendPacket(packet);
        } else {
            System.out.println("[NPC] Couldn't get field for NPC from Method addToTablist()!");
        }
        return this;
    }

    public NPC removeFromTablist() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeFromTablist(player);
        }
        return this;
    }


    public void damage(Player player) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        setValue(packet, "a", getEntityID());
        setValue(packet, "b", 1);
        sendPacket(packet, player);
        player.playSound(getLocation(), Sound.HURT_FLESH, 1.0F, 1.0F);
    }

    public void damage() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            damage(player);
        }
    }

    public void swingArm(Player player) {
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
        setValue(packet, "a", getEntityID());
        setValue(packet, "b", 0);
        sendPacket(packet, player);
    }

    public void resetMovement() {
        DataWatcher w = new DataWatcher(null);
        w.a(0, (byte) 0);
        w.a(1, (short) 0);
        w.a(8, (byte) 0);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(getEntityID(), w, true);
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(packet, player);
        }
    }

    public void lookAt(Location location) {
        Location npcLocation = location.setDirection(location.subtract(getLocation()).toVector());
        headRotation(npcLocation.getYaw(), npcLocation.getPitch());
    }

    public void lookAt(Location location, Player player) {
        Location npcLocation = location.setDirection(location.subtract(getLocation()).toVector());
        headRotation(npcLocation.getYaw(), npcLocation.getPitch(), player);
    }

    public void updateItems(org.bukkit.inventory.ItemStack inHand, org.bukkit.inventory.ItemStack boots, org.bukkit.inventory.ItemStack leggins, org.bukkit.inventory.ItemStack chestplate, org.bukkit.inventory.ItemStack helmet) {
        if(inHand != null)
            this.handHeld = inHand;
        if(boots != null)
            this.boots = boots;
        if(leggins != null)
            this.leggins = leggins;
        if(chestplate != null)
            this.chestplate = chestplate;
        if(helmet != null)
            this.helmet = helmet;

        PacketPlayOutEntityEquipment[] packets = { new PacketPlayOutEntityEquipment(getEntityID(), 1, CraftItemStack.asNMSCopy(this.helmet)), new PacketPlayOutEntityEquipment(getEntityID(), 2, CraftItemStack.asNMSCopy(this.chestplate)), new PacketPlayOutEntityEquipment(getEntityID(), 3, CraftItemStack.asNMSCopy(this.leggins)), new PacketPlayOutEntityEquipment(getEntityID(), 4, CraftItemStack.asNMSCopy(this.boots)), new PacketPlayOutEntityEquipment(getEntityID(), 0, CraftItemStack.asNMSCopy(this.handHeld)) };

        for(int i = 0; i < packets.length; i++) {
            sendPacket(packets[i]);
        }
    }


    public void move(double x, double y, double z, float yaw, float pitch){
        sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getEntityID(), (byte) calc(x), (byte) calc(y), (byte) calc(z), toAngle(yaw), toAngle(pitch), true));
        this.location.add(calc(x) / 32D, calc(y) / 32D, calc(z) / 32D);
        this.location.setYaw(yaw);
        this.location.setPitch(pitch);
    }

    public void sprint() {
        DataWatcher w = new DataWatcher(null);
        w.a(0, (byte) 8);
        w.a(1, (short) 0);
        w.a(8, (byte) 0);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(getEntityID(), w, true);
        sendPacket(packet);
    }

    public void block() {
        DataWatcher w = new DataWatcher(null);
        w.a(0, (byte) 16);
        w.a(1, (short) 0);
        w.a(6, (byte) 0);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(getEntityID(), w, true);
        sendPacket(packet);
    }

    private byte toAngle(float value){
        return (byte) ((int) (value * 256.0F / 360.0F));
    }

    private int calc(double value){
        return (int) Math.floor(value * 32.0D);
    }

    public void swingArm() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            swingArm(player);
        }
    }

    public void sneak(Player player, Boolean b) {
        DataWatcher w = new DataWatcher(null);
        w.a(0, (byte) 2);
        w.a(1, (short) 0);
        w.a(8, (byte) 0);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(getEntityID(), w, b);
        sendPacket(packet, player);
    }

    public void sneak(Player player) {
        sneak(player, true);
    }

    public void unSneak(Player player) {
        sneak(player, false);
    }


    public void sneak() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sneak(player);
        }
    }

    public void unSneak() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            unSneak(player);
        }
    }

    public NPC removeFromTablist(Player player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameprofile, 1, EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameprofile.getName())[0]);

        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) getValue(packet, "b");
        if (players != null) {
            players.add(data);
            setValue(packet, "a", PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
            setValue(packet, "b", players);

            sendPacket(packet, player);
        } else {
            System.out.println("[NPC] Couldn't get field for NPC from Method removeFromTablist(Player player)!");
        }
        return this;
    }

    private int getFixLocation(double pos) {
        return (int) MathHelper.floor(pos * 32.0D);
    }

    private byte getFixRotation(float yawpitch) {
        return (byte) ((int) (yawpitch * 256.0F / 360.0F));
    }

    private void setValue(Object obj, String name, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getValue(Object obj, String name) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendPacket(Packet<?> packet, Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private void sendPacket(Packet<?> packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(packet, player);
        }
    }

    public Map<String, Object> encode() {
        Map<String, Object> map = new HashMap<>();

        map.put("X", location.getX());
        map.put("Y", location.getY());
        map.put("Z", location.getZ());
        map.put("Pitch", location.getPitch());
        map.put("Yaw", location.getYaw());
        map.put("World", location.getWorld().getName());

        map.put("name", gameprofile.getName());
        map.put("entityID", entityID);
        map.put("UUID", gameprofile.getId());
        map.put("health", health);

        String value = "";
        String signatur = "";
        for (Property property : gameprofile.getProperties().get("textures")) {
            value = property.getValue();
            signatur = property.getSignature();
        }
        map.put("value", value);
        map.put("signatur", signatur);

        return map;
    }

    public static NPC decode(Map<String, Object> map) {
        String name = (String) map.get("name");
        Integer entityID = (Integer) map.get("entityID");
        UUID uuid = (UUID) map.get("UUID");

        World world = Bukkit.getWorld((String) map.get("World"));
        Double x = (Double) map.get("X");
        Double y = (Double) map.get("Y");
        Double z = (Double) map.get("Z");
        Float pitch = (Float) map.get("Pitch");
        Float yaw = (Float) map.get("Yaw");
        Location location = new Location(world, x, y, z, yaw, pitch);

        NPC npc = new NPC(name, entityID, uuid, location);
        npc.health = (Float) map.get("health");
        String value = (String) map.get("value");
        String signatur = (String) map.get("signatur");
        npc.gameprofile.getProperties().put("textures", new Property("textures", value, signatur));
        return npc;
    }

    public String toString() {
        return this.encode().toString();
    }

    public static class SkinFetcher {

        private static Map<String, String> values = Maps.newConcurrentMap();
        private static Map<String, String> signatures = Maps.newConcurrentMap();
        private static List<String> skins = Lists.newLinkedList();

        public String getSkinValue(String uuid) {
            return data(uuid)[0];
        }

        public String getSkinSignature(String uuid) {
            return data(uuid)[1];
        }

        public String[] data(String uuid) {
            String[] data = new String[2];
            if (!skins.contains(uuid)) {
                skins.add(uuid);
                try {
                    URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                    URLConnection uc = url.openConnection();
                    uc.setUseCaches(false);
                    uc.setDefaultUseCaches(false);
                    uc.addRequestProperty("User-Agent", "Mozilla/5.0");
                    uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
                    uc.addRequestProperty("Pragma", "no-cache");
                    String json = new Scanner(uc.getInputStream(), "UTF-8").useDelimiter("\\A").next();
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(json);
                    JSONArray properties = (JSONArray) ((JSONObject) obj).get("properties");
                    for (int i = 0; i < properties.size(); i++) {
                        try {
                            JSONObject property = (JSONObject) properties.get(i);
                            String name = (String) property.get("name");
                            String value = (String) property.get("value");
                            String signature = property.containsKey("signature") ? (String) property.get("signature") : null;
                            values.put(uuid, value);
                            signatures.put(uuid, signature);
                            data[0] = value;
                            data[1] = signature;
                        } catch (Exception e) {}
                    }
                } catch (Exception e) {}
            } else {
                data[0] = values.get(uuid);
                data[1] = signatures.get(uuid);
            }
            return data;
        }

    }
}
