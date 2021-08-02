package de.lystx.hytoracloud.driver.commons.wrapped;



import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class ItemObject extends WrappedObject<Item, ItemObject> implements Item {

    private static final long serialVersionUID = -2678114340484151627L;

    /**
     * The lore of this item
     */
    @Setter
    private List<String> lore;

    /**
     * The sub id
     */
    private int id;

    /**
     * The material
     */
    private String material;

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

    public ItemObject() {
        this(new LinkedList<>(), null, 1, (short) 0);
    }

    public ItemObject(String material) {
        this(new LinkedList<>(), material, 1, (short) 0);
    }

    public ItemObject(List<String> lore, String material, int amount, short id) {
        this.lore = lore;
        this.material = material;
        this.amount = amount;
        this.id = id;
        this.displayName = "";
    }

    @Override
    public Item material(Enum<?> material) {
        return this.material(material.name());
    }

    @Override
    public Item material(String material) {
        this.material = material;
        return this;
    }


    @Override
    public Item id(int id) {
        this.id = id;
        return this;
    }

    @Override
    public Item amount(int value) {
        this.amount = value;
        return this;
    }

    @Override
    public Item slot(int slot) {
        this.preInventorySlot = slot;
        return this;
    }

    @Override
    public Item noName() {
        return this.display("§8");
    }

    @Override
    public Item glow() {
        this.glow = true;
        return this;
    }

    @Override
    public Item skullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }

    @Override
    public Item lore(String line) {
        this.lore.add(line);
        return this;
    }

    @Override
    public Item lore(List<String> lines) {
        this.lore.addAll(lines);
        return this;
    }

    @Override
    public Item display(String name) {
        this.displayName = name;
        return this;
    }

    @Override
    public Item unbreakable() {
        this.unbreakable = true;
        return this;
    }

    @Override
    Class<ItemObject> getWrapperClass() {
        return ItemObject.class;
    }

    @Override
    Class<Item> getInterface() {
        return Item.class;
    }
}
