package de.lystx.hytoracloud.bridge.bukkit.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Item {

    private ItemStack item;
    private List<String> lore = new ArrayList<String>();
    private ItemMeta meta;
    private short subid;
    public Item(Material mat, short subid, int amount) {
        this.subid = subid;
        item = new ItemStack(mat, amount, subid);
        meta = item.getItemMeta();
    }


    public Item(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }
    public Item(Material mat, short subid) {
        this.subid = subid;
        item = new ItemStack(mat, 1, subid);
        meta = item.getItemMeta();
    }

    public Item(Material mat, int amount) {
        item = new ItemStack(mat, amount, (short)0);
        meta = item.getItemMeta();
    }

    public Item(Material mat) {
        item = new ItemStack(mat, 1, (short)0);
        meta = item.getItemMeta();
    }

    public Item setAmount(int value) {
        item.setAmount(value);
        return this;
    }

    public Item setNoName() {
        meta.setDisplayName(" ");
        return this;
    }
    public Item setGlow() {
        meta.addEnchant( Enchantment.DURABILITY, 1, true);
        meta.addItemFlags( ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public Item setData(short data) {
        item.setDurability(data);
        return this;
    }

    public Item addLoreLine(String line) {
        lore.add(line);
        return this;
    }

    public Item addLoreArray(List<String> lines) {
        lore.addAll(lines);
        return this;
    }

    public Item addLoreAll(List<String> lines) {
        lore.addAll(lines);
        return this;
    }

    public Item setDisplayName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public Item setSkullOwner(String owner) {
        ((SkullMeta )meta).setOwner(owner);
        return this;
    }

    public Item setColor(Color c) {
        ((LeatherArmorMeta )meta).setColor(c);
        return this;
    }

    public Item setBannerColor(DyeColor c) {
        ((BannerMeta )meta).setBaseColor(c);
        return this;
    }

    public Item setUnbreakable(boolean value) {
        //meta.spigot().setUnbreakable(value);
        return this;
    }

    public Item addEnchantment(Enchantment ench, int lvl) {
        meta.addEnchant(ench, lvl, true);
        return this;
    }

    public Item addItemFlag(ItemFlag flag) {
        meta.addItemFlags(flag);
        return this;
    }


    public Item addLeatherColor(Color color) {
        ((LeatherArmorMeta ) meta).setColor( color );
        return this;
    }

    public Item setSkullTextures(String value, String signature) {
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


    public ItemStack build() {
        if(!lore.isEmpty()) {
            meta.setLore(lore);
        }
        String name = meta.getDisplayName();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }


}