package de.lystx.hytoracloud.bridge.spigot.bukkit.impl.handler;

import de.lystx.hytoracloud.bridge.spigot.bukkit.BukkitBridge;
import de.lystx.hytoracloud.bridge.spigot.bukkit.utils.BukkitItem;
import de.lystx.hytoracloud.driver.CloudDriver;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.Item;
import de.lystx.hytoracloud.driver.commons.minecraft.world.MinecraftLocation;
import de.lystx.hytoracloud.driver.commons.requests.base.DriverRequest;
import de.lystx.hytoracloud.driver.commons.storage.JsonObject;
import de.lystx.hytoracloud.networking.elements.packet.Packet;
import de.lystx.hytoracloud.networking.elements.packet.handler.PacketHandler;

import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class BukkitHandlerCloudPlayer implements PacketHandler {

    public BukkitHandlerCloudPlayer() {
        CloudDriver.getInstance().getRequestManager().registerRequestHandler(new Consumer<DriverRequest<?>>() {

            @SneakyThrows
            @Override
            public void accept(DriverRequest<?> driverRequest) {
                JsonObject<?> document = driverRequest.getDocument();
                if (driverRequest.getKey().equalsIgnoreCase("PLAYER_SEND_ACTION_BAR")) {
                    UUID uuid = UUID.fromString(document.getString("uniqueId"));
                    String message = document.getString("message");
                    Player player = Bukkit.getPlayer(uuid);

                    if (player == null || message == null) {
                        return;
                    }

                    String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
                    nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);

                    if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {

                        Method getSpigot = player.getClass().getDeclaredMethod("spigot"); getSpigot.setAccessible(true);
                        Object spigot = getSpigot.invoke(player);
                        Method sendMessage = spigot.getClass().getDeclaredMethod("sendMessage", ChatMessageType.class, TextComponent.class); sendMessage.setAccessible(true);

                        sendMessage.invoke(spigot, ChatMessageType.ACTION_BAR, new TextComponent(message));
                        driverRequest.createResponse(Boolean.class).data(true).send();
                        return;
                    }

                    try {
                        Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
                        Object craftPlayer = craftPlayerClass.cast(player);

                        Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
                        Class<?> p = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
                        Object packetPlayOutChat;
                        Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
                        Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

                        Method method = null;
                        if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

                        Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
                        packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

                        Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
                        Object iCraftPlayer = handle.invoke(craftPlayer);
                        Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
                        Object playerConnection = playerConnectionField.get(iCraftPlayer);
                        Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", p);
                        sendPacket.invoke(playerConnection, packetPlayOutChat);
                        driverRequest.createResponse(Boolean.class).data(true).send();
                    } catch (Exception ex) {
                        driverRequest.createResponse(Boolean.class).exception(ex).data(false).send();
                    }
                } else if (driverRequest.getKey().equalsIgnoreCase("PLAYER_OPEN_INVENTORY")) {

                    UUID uuid = UUID.fromString(document.getString("uniqueId"));
                    Player bukkitPlayer = Bukkit.getPlayer(uuid);
                    if (bukkitPlayer == null) {
                        return;
                    }

                    JsonObject<?> doc = document.getObject("inventory");
                    JsonObject<?> items = doc.getObject("items");

                    Map<Integer, Item> itemMap = new HashMap<>();
                    String title = doc.getString("title");
                    int rows = doc.getInteger("rows");

                    for (String s : items.keySet()) {
                        JsonObject<?> item = items.getObject(s);
                        int slot = Integer.parseInt(s);

                        Item build = Item.builder();

                        build.display(item.getString("displayName"));
                        build.skullOwner(item.getElement("skullOwner").isJsonNull() ? null : item.getString("skullOwner"));

                        build.lore(item.getStringList("lore"));
                        build.id(item.getInteger("id"));
                        build.amount(item.getInteger("amount"));
                        build.slot(item.getInteger("preInventorySlot"));
                        build.material(item.getString("material"));

                        if (item.getBoolean("unbreakable")) {
                            build.unbreakable();
                        }
                        if (item.getBoolean("glow")) {
                            build.glow();
                        }

                        itemMap.put(slot, build);
                    }

                    org.bukkit.inventory.Inventory inv = Bukkit.createInventory(bukkitPlayer, rows * 9, title);
                    for (Integer slot : itemMap.keySet()) {
                        if (slot < 0 || slot > inv.getSize()) {
                            continue;
                        }
                        inv.setItem(slot, BukkitItem.fromCloudItem(itemMap.get(slot)));
                    }
                    bukkitPlayer.openInventory(inv);

                    driverRequest.createResponse(Boolean.class).data(true).send();
                } else if (driverRequest.equalsIgnoreCase("PLAYER_TELEPORT_LOCATION")) {

                    UUID uuid = UUID.fromString(document.getString("uniqueId"));
                    Player bukkitPlayer = Bukkit.getPlayer(uuid);
                    if (bukkitPlayer == null) {
                        return;
                    }
                    try {
                        MinecraftLocation location = document.get("location", MinecraftLocation.class);
                        Location location1 = BukkitBridge.getInstance().fromLocation(location);
                        bukkitPlayer.teleport(location1);
                        driverRequest.createResponse().data(true).send();
                    } catch (Exception e) {
                        driverRequest.createResponse().exception(e).send();
                    }
                } else if (driverRequest.equalsIgnoreCase("PLAYER_GET_LOCATION")) {
                    UUID uuid = UUID.fromString(document.getString("uniqueId"));
                    Player bukkitPlayer = Bukkit.getPlayer(uuid);
                    if (bukkitPlayer == null) {
                        return;
                    }
                    try {
                        driverRequest.createResponse().data(BukkitBridge.getInstance().toLocation(bukkitPlayer.getLocation())).send();
                    } catch (Exception e) {
                        driverRequest.createResponse().exception(e).send();
                    }
                } else if (driverRequest.equalsIgnoreCase("PLAYER_SEND_TITLE")) {

                    UUID uuid = UUID.fromString(document.getString("uniqueId"));
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) {
                        return;
                    }
                    player.sendTitle(document.getString("title"), document.getString("subtitle"));
                    driverRequest.createResponse().data(true).send();

                } else if (driverRequest.equalsIgnoreCase("PLAYER_PLAY_SOUND")) {
                    UUID uuid = UUID.fromString(document.getString("uniqueId"));
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) {
                        return;
                    }
                    Sound sound = Sound.valueOf(document.getString("sound"));
                    player.playSound(player.getLocation(), sound, document.getFloat("v1"), document.getFloat("v2"));
                    driverRequest.createResponse().data(true).send();
                }
            }
        });
    }

    @SneakyThrows
    public void handle(Packet packet) {
    }
}
