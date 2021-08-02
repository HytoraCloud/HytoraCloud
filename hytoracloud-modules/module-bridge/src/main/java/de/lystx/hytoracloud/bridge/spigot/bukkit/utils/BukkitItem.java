package de.lystx.hytoracloud.bridge.spigot.bukkit.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.Item;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class BukkitItem {

    /**
     * The itemstack from bukkit
     *
     */
    private final ItemStack item;

    /**
     * The lore
     */
    private final List<String> lore = new LinkedList<>();

    /**
     * The item meta for display name etc
     */
    private final ItemMeta meta;

    /**
     * The sub id for color for example
     */
    private short subid;


    public BukkitItem(Material mat, short subid, int amount) {
        this.subid = subid;
        this.item = new ItemStack(mat, amount, subid);
        this.meta = item.getItemMeta();
    }


    public BukkitItem(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public BukkitItem(Material mat, short subid) {
        this.subid = subid;
        this.item = new ItemStack(mat, 1, subid);
        this.meta = item.getItemMeta();
    }

    public BukkitItem(Material mat, int amount) {
        this.item = new ItemStack(mat, amount, (short)0);
        this.meta = item.getItemMeta();
    }

    public BukkitItem(Material mat) {
        this.item = new ItemStack(mat, 1, (short)0);
        this.meta = item.getItemMeta();
    }

    /**
     * Sets the amount of the itemstack
     *
     * @param value the amount
     * @return current item
     */
    public BukkitItem setAmount(int value) {
        item.setAmount(value);
        return this;
    }

    /**
     * Sets the display name to empty
     *
     * @return current item
     */
    public BukkitItem setNoName() {
        meta.setDisplayName(" ");
        return this;
    }

    /**
     * Makes the item glow
     *
     * @return current item
     */
    public BukkitItem setGlow() {
        meta.addEnchant( Enchantment.DURABILITY, 1, true);
        meta.addItemFlags( ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    /**
     * Sets a data for this item
     *
     * @param data the data
     * @return current item
     */
    public BukkitItem setData(short data) {
        item.setDurability(data);
        return this;
    }

    /**
     * Adds a lore array
     *
     * @param lines the lines
     * @return current item
     */
    public BukkitItem addLores(List<String> lines) {
        lore.addAll(lines);
        return this;
    }

    /**
     * Sets the display name of the item
     *
     * @param name the name
     * @return current item
     */
    public BukkitItem setDisplayName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    /**
     * Sets the skull owner
     *
     * @param owner the owner
     * @return current item
     */
    public BukkitItem setSkullOwner(String owner) {
        ((SkullMeta )meta).setOwner(owner);
        return this;
    }

    /**
     * Sets the banner color
     *
     * @param c the color
     * @return current item
     */
    public BukkitItem setBannerColor(DyeColor c) {
        ((BannerMeta )meta).setBaseColor(c);
        return this;
    }

    /**
     * Sets the item to unbreakable
     *
     * @param value the value
     * @return current item
     */
    public BukkitItem setUnbreakable(boolean value) {
        //meta.spigot().setUnbreakable(value);
        return this;
    }


    /**
     * Sets a leather color
     *
     * @param color the color
     * @return current item
     */
    public BukkitItem addLeatherColor(Color color) {
        ((LeatherArmorMeta ) meta).setColor( color );
        return this;
    }

    public BukkitItem setSkullTextures(String value, String signature) {
        try {
            SkullMeta skullMeta = (SkullMeta) meta;
            final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), (String) null);
            gameProfile.getProperties().put("textures", new Property("textures", value, signature));
            final Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, gameProfile);
            item.setItemMeta(skullMeta);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Builds the item
     *
     * @return item stack
     */
    public ItemStack build() {
        if(!lore.isEmpty()) {
            meta.setLore(lore);
        }
        String name = meta.getDisplayName();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }


    /**
     * Transforms a CloudItem to a Bukkit ItemStack
     *
     * @param cloudItem the clouditem
     * @return itemstack
     */
    public static ItemStack fromCloudItem(Item cloudItem) {
        if (cloudItem == null) {
            return null;
        }
        BukkitItem bukkitItemStack = new BukkitItem(
                Material.valueOf(cloudItem.getMaterial()),
                (short) cloudItem.getId(),
                cloudItem.getAmount()
        );
        bukkitItemStack.addLores(cloudItem.getLore());
        bukkitItemStack.setUnbreakable(cloudItem.isUnbreakable());
        if (cloudItem.isGlow()) {
            bukkitItemStack.setGlow();
        }
        bukkitItemStack.setDisplayName(cloudItem.getDisplayName());
        if (cloudItem.getSkullOwner() != null) {
            bukkitItemStack.setSkullOwner(cloudItem.getSkullOwner());
        }
        return bukkitItemStack.build();
    }

}