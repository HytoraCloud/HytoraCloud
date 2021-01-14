package de.lystx.cloudapi.bukkit.manager.npc.impl;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Hologram {

    private Location location;
    private List<HoloLine> lines;
    private List<Player> loadedPlayers;
    private Integer id;
    private String name;
    private HologramManager hologramManager = new HologramManager();

    public Hologram(Location location) {
        this.lines = new ArrayList<>();
        this.loadedPlayers = new ArrayList<>();
        id = -1;
        if (location != null) {
            this.location = location;
        } else {
            this.location = new Location(Bukkit.getWorld("world"), 500D, 200D, 500D);
        }
    }

    public Hologram setName(String name) {
        this.name = name;
        return this;
    }

    public Hologram append(Integer hologramid) {
        this.id = hologramid;
        return this;
    }

    public Hologram append(String text) {
        this.lines.add(new TextLine(text, getRandomEntityID()));
        return this;
    }

    public Hologram append(ItemStack itemStack) {
        this.lines.add(new ItemLine(getRandomEntityID(), getRandomEntityID(), itemStack));
        return this;
    }

    public void send(Player player) {
        hologramManager.addPlayerToHologram(player, this);
        checkSending(player);
    }

    public void build() {
        Bukkit.getOnlinePlayers().forEach(this::send);
    }

    public int getRandomEntityID() {
        return new Random().nextInt(100000);
    }

    public Hologram delete(Player player) {
        hologramManager.removePlayerHologram(player, this);
        removeFromPlayer(player);
        return this;
    }

    public Hologram delete() {
        Bukkit.getOnlinePlayers().forEach(this::delete);
        return this;
    }

    public void checkSending(Player player) {
        if (isInRange(player)) {
            sendToPlayer(player);
        } else {
            removeFromPlayer(player);
        }
    }

    private void sendToPlayer(Player player) {
        if (isPlayerLoaded(player))
            return;
        this.loadedPlayers.add(player);
        double locX = this.location.getX();
        double locY = this.location.getY();
        double locZ = this.location.getZ();
        PlayerConnection playerConnection = (((CraftPlayer)player).getHandle()).playerConnection;
        for (HoloLine holoLine : this.lines) {
            Location spawnLocation = new Location(this.location.getWorld(), locX, locY, locZ);
            Packet[] spawnPackets;
            for (int length = (spawnPackets = holoLine.getSpawnPackets(spawnLocation)).length, i = 0; i < length; i++) {
                Packet packet = spawnPackets[i];
                playerConnection.sendPacket(packet);
            }
            if (holoLine instanceof ItemLine) {
                locY--;
                continue;
            }
            locY -= 0.3D;
        }
    }

    private void removeFromPlayer(Player player) {
        id = -1;
        if (!isPlayerLoaded(player))
            return;
        for (HoloLine holoLine : this.lines) {
            PlayerConnection playerConnection = (((CraftPlayer)player).getHandle()).playerConnection;
            Packet[] despawnPackets;
            for (int length = (despawnPackets = holoLine.getDespawnPackets()).length, i = 0; i < length; i++) {
                Packet packet = despawnPackets[i];
                playerConnection.sendPacket(packet);
            }
        }
        this.loadedPlayers.remove(player);
    }

    public boolean isPlayerLoaded(Player player) {
        return this.loadedPlayers.contains(player);
    }

    public boolean isInRange(Player player) {
        return (this.location.getWorld() == player.getLocation().getWorld() &&
                this.location.distance(player.getLocation()) <= 48.0D);
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return this.location;
    }

    public List<HoloLine> getLines() {
        return this.lines;
    }

    public List<Player> getLoadedPlayers() {
        return this.loadedPlayers;
    }

    public Integer getId() {
        return id;
    }

    public class TextLine implements HoloLine {
        private PacketHelper packetHelper = new PacketHelper();
        private String text;
        private int entityID;

        public TextLine(String text, int entityID) {
            this.text = text;
            this.entityID = entityID;
        }

        public Packet[] getSpawnPackets(Location location) {
            Packet<?> spawnPacket = packetHelper.spawnArmorStand(location, this.text, this.entityID);
            return new Packet[]{spawnPacket};
        }

        public Packet[] getDespawnPackets() {
            return new Packet[]{packetHelper.destroyEntity(this.entityID)};
        }
    }

    public class PacketHelper {
        public Packet<?> spawnArmorStand(Location location, String name, int entityID) {
            EntityArmorStand entityArmorStand = new EntityArmorStand(
                    (World)((CraftWorld)location.getWorld()).getHandle());
            entityArmorStand.setLocation(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
            if (name.equals("clearline")) {
                entityArmorStand.setCustomNameVisible(false);
            } else {
                entityArmorStand.setCustomNameVisible(true);
                entityArmorStand.setCustomName(name);
            }
            entityArmorStand.setInvisible(true);
            entityArmorStand.setGravity(true);
            try {
                Field field = Entity.class.getDeclaredField("id");
                field.setAccessible(true);
                field.setInt(entityArmorStand, entityID);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
            return (Packet<?>)new PacketPlayOutSpawnEntityLiving((EntityLiving)entityArmorStand);
        }

        public EntityItem createItem(Location location, ItemStack itemStack, int entityID) {
            EntityItem entityItem = new EntityItem((World)((CraftWorld)location.getWorld()).getHandle());
            entityItem.setItemStack(CraftItemStack.asNMSCopy(itemStack));
            entityItem.setLocation(location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
            try {
                Field field = Entity.class.getDeclaredField("id");
                field.setAccessible(true);
                field.setInt(entityItem, entityID);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
            return entityItem;
        }

        public Packet<?> itemSpawn(EntityItem entityItem) {
            return (Packet<?>)new PacketPlayOutSpawnEntity((Entity)entityItem, 2);
        }

        public Packet<?> itemMeta(int itemID, DataWatcher dataWatcher) {
            return (Packet<?>)new PacketPlayOutEntityMetadata(itemID, dataWatcher, true);
        }

        public Packet<?> attachItemToArmorStand(int itemID, int armorStandID) {
            PacketPlayOutAttachEntity packetPlayOutAttachEntity = new PacketPlayOutAttachEntity();
            try {
                Field unknown = PacketPlayOutAttachEntity.class.getDeclaredField("a");
                unknown.setAccessible(true);
                unknown.set(packetPlayOutAttachEntity, Integer.valueOf(0));
                Field entityID = PacketPlayOutAttachEntity.class.getDeclaredField("b");
                entityID.setAccessible(true);
                entityID.set(packetPlayOutAttachEntity, Integer.valueOf(itemID));
                Field vehicleID = PacketPlayOutAttachEntity.class.getDeclaredField("c");
                vehicleID.setAccessible(true);
                vehicleID.set(packetPlayOutAttachEntity, Integer.valueOf(armorStandID));
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            }
            return (Packet<?>)packetPlayOutAttachEntity;
        }

        public Packet<?> destroyEntity(int entityID) {
            return (Packet<?>)new PacketPlayOutEntityDestroy(new int[] { entityID });
        }
    }


    public class ItemLine implements HoloLine {
        
        private int armorStandID;
        private int itemID;
        private ItemStack itemStack;
        private PacketHelper packetHelper = new PacketHelper();

        public ItemLine(int armorStandID, int itemID, ItemStack itemStack) {
            this.armorStandID = armorStandID;
            this.itemID = itemID;
            this.itemStack = itemStack;
        }

        @SuppressWarnings("rawtypes")
        public Packet[] getSpawnPackets(Location location) {
            EntityItem entityItem = packetHelper.createItem(location, this.itemStack, this.itemID);
            return new Packet[] { packetHelper.spawnArmorStand(location, "clearline", this.armorStandID), packetHelper.itemSpawn(entityItem), packetHelper.itemMeta(this.itemID, entityItem.getDataWatcher()), packetHelper.attachItemToArmorStand(this.itemID, this.armorStandID) };
        }

        @SuppressWarnings("rawtypes")
        public Packet[] getDespawnPackets() {
            return new Packet[] { packetHelper.destroyEntity(this.itemID), packetHelper.destroyEntity(this.armorStandID) };
        }
    }



    public interface HoloLine {
        Packet[] getSpawnPackets(Location paramLocation);

        @SuppressWarnings("rawtypes")
        Packet[] getDespawnPackets();
    }


    public class HologramManager {
        private HashMap<Player, List<Hologram>> playerHolograms = new HashMap<>();

        public void addPlayerToHologram(Player player, Hologram hologram) {
            if (!playerHolograms.containsKey(player))
                playerHolograms.put(player, new ArrayList<>());
            List<Hologram> holograms = playerHolograms.get(player);
            holograms.add(hologram);
            playerHolograms.put(player, holograms);
        }

        public void handlePlayerQuit(Player player) {
            if (playerHolograms.containsKey(player)) {
                for (Hologram hologram : playerHolograms.get(player))
                    hologram.getLoadedPlayers().remove(player);
                playerHolograms.remove(player);
            }
        }

        public void removePlayerHologram(Player player, Hologram hologram) {
            if (!playerHolograms.containsKey(player))
                return;
            List<Hologram> holograms = playerHolograms.get(player);
            holograms.remove(hologram);
            playerHolograms.put(player, holograms);
        }

        public void updatePlayerView(Player player) {
            if (!playerHolograms.containsKey(player))
                return;
            for (Hologram hologram : playerHolograms.get(player))
                hologram.checkSending(player);
        }

        public HashMap<Player, List<Hologram>> getPlayerHolograms() {
            return playerHolograms;
        }
    }
}
