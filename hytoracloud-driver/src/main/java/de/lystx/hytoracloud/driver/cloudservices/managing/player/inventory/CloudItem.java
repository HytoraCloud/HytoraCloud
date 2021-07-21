package de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory;



import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@Getter
public class CloudItem implements Serializable {

    private static final long serialVersionUID = -2678114340484151627L;
    /**
     * The lore of this item
     */
    @Setter
    private List<String> lore;

    /**
     * The sub id
     */
    private final short id;

    /**
     * The material
     */
    private final String material;

    /**
     * The slot it should be put in
     */
    private int preInventorySlot;

    /**
     * The displayname
     */
    private String displayName;

    /**
     * The amount
     */
    private int amount;

    /**
     * If its unbreakable
     */
    private boolean unbreakable;

    /**
     * If it should glow
     */
    private boolean glow;

    /**
     * The skull owner if it's a skull
     */
    private String skullOwner;

    public CloudItem(Enum<?> material, short subid, int amount) {
        this(new LinkedList<>(), material.name(), amount, subid);
    }

    public CloudItem(String material, short subid, int amount) {
        this(new LinkedList<>(), material, amount, subid);
    }

    public CloudItem(Enum<?> material, int amount, short subid) {
        this(new LinkedList<>(), material.name(), amount, subid);
    }

    public CloudItem(String material, int amount, short subid) {
        this(new LinkedList<>(), material, amount, subid);
    }

    public CloudItem(Enum<?> material, short subid) {
        this(new LinkedList<>(), material.name(), 1, subid);
    }

    public CloudItem(String material, short subid) {
        this(new LinkedList<>(), material, 1, subid);
    }

    public CloudItem(Enum<?> material, int amount) {
        this(new LinkedList<>(), material.name(), amount, (short) 0);
    }

    public CloudItem(String material, int amount) {
        this(new LinkedList<>(), material, amount, (short) 0);
    }

    public CloudItem(Enum<?> material) {
        this(new LinkedList<>(), material.name(), 1, (short) 0);
    }

    public CloudItem(String material) {
        this(new LinkedList<>(), material, 1, (short) 0);
    }

    public CloudItem(List<String> lore, String material, int amount, short id) {
        this.lore = lore;
        this.material = material;
        this.amount = amount;
        this.id = id;
        this.displayName = "";
    }

    /**
     * Manually changes the amount
     *
     * @param value the amount
     * @return current item
     */
    public CloudItem amount(int value) {
        this.amount = value;
        return this;
    }

    /**
     * Sets the pre slot
     *
     * @param slot the slot
     * @return current item
     */
    public CloudItem slot(int slot) {
        this.preInventorySlot = slot;
        return this;
    }

    /**
     * Sets an empty name
     *
     * @return current item
     */
    public CloudItem noName() {
        this.displayName = "§8";
        return this;
    }

    /**
     * Makes the item glow
     *
     * @return current item
     */
    public CloudItem glow() {
        this.glow = true;
        return this;
    }

    /**
     * Sets Skull Owner
     *
     * @param skullOwner the owner
     * @return current item
     */
    public CloudItem skullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }

    /**
     * Adds a single lore line
     *
     * @param line the line
     * @return current item
     */
    public CloudItem lore(String line) {
        this.lore.add(line);
        return this;
    }

    /**
     * Adds a whole Lore
     *
     * @param lines the lines
     * @return current item
     */
    public CloudItem lore(List<String> lines) {
        this.lore.addAll(lines);
        return this;
    }

    /**
     * Sets displayName
     *
     * @param name the display name
     * @return current item
     */
    public CloudItem display(String name) {
        this.displayName = name;
        return this;
    }

    /**
     * Makes item unbreakable
     *
     * @return current item
     */
    public CloudItem unbreakable() {
        this.unbreakable = true;
        return this;
    }

}
