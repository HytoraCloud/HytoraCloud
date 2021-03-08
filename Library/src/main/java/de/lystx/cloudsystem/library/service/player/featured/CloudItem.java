package de.lystx.cloudsystem.library.service.player.featured;

import lombok.Getter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Getter
public class CloudItem implements Serializable {


    private final List<String> lore;
    private final short id;
    private final String material;

    private String displayName;
    private int amount;
    private boolean unbreakable;
    private boolean glow;

    private String skullOwner;


    /**
     * Constructs a CloudItem
     * @param material
     * @param subid
     * @param amount
     */
    public CloudItem(Enum<?> material, short subid, int amount) {
        this(new LinkedList<>(), material.name(), amount, subid);
    }

    /**
     * Constructs a CloudItem
     * @param material
     * @param subid
     * @param amount
     */
    public CloudItem(Enum<?> material, int amount, short subid) {
        this(new LinkedList<>(), material.name(), amount, subid);
    }

    /**
     * Constructs a CloudItem
     * @param material
     * @param subid
     */
    public CloudItem(Enum<?> material, short subid) {
        this(new LinkedList<>(), material.name(), 1, subid);
    }

    /**
     * Constructs a CloudItem
     * @param material
     * @param amount
     */
    public CloudItem(Enum<?> material, int amount) {
        this(new LinkedList<>(), material.name(), amount, (short) 0);
    }

    /**
     * Constructs a CloudItem
     * @param material
     */
    public CloudItem(Enum<?> material) {
        this(new LinkedList<>(), material.name(), 1, (short) 0);
    }

    /**
     * Constructs a CloudItem
     * @param lore
     * @param material
     * @param amount
     * @param id
     */
    public CloudItem(List<String> lore, String material, int amount, short id) {
        this.lore = lore;
        this.material = material;
        this.amount = amount;
        this.id = id;
        this.displayName = "";
    }

    /**
     * Manually changes the amount
     * @param value
     * @return
     */
    public CloudItem setAmount(int value) {
        this.amount = value;
        return this;
    }

    /**
     * Sets an empty name
     * @return
     */
    public CloudItem noName() {
        this.displayName = "§8";
        return this;
    }

    /**
     * Makes the item glow
     * @return
     */
    public CloudItem glow() {
        this.glow = true;
        return this;
    }

    /**
     * Sets Skull Owner
     * @param skullOwner
     * @return
     */
    public CloudItem setSkullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }

    /**
     * Adds a single lore line
     * @param line
     * @return
     */
    public CloudItem addLore(String line) {
        this.lore.add(line);
        return this;
    }

    /**
     * Adds a whole Lore
     * @param lines
     * @return
     */
    public CloudItem addLoreArray(List<String> lines) {
        this.lore.addAll(lines);
        return this;
    }

    /**
     * Sets displayName
     * @param name
     * @return
     */
    public CloudItem setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    /**
     * Makes item unbreakable
     * @return
     */
    public CloudItem unbreakable() {
        this.unbreakable = true;
        return this;
    }

}
