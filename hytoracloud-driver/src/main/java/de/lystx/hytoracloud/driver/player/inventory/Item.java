package de.lystx.hytoracloud.driver.player.inventory;

import de.lystx.hytoracloud.driver.wrapped.ItemObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public interface Item extends Serializable {

    /**
     * Creates a new Builder
     *
     * @return the builder
     */
    static Item builder() {
        Item itemObject = new ItemObject();
        itemObject.amount(1);
        itemObject.material("BARRIER");
        itemObject.display("§8");
        itemObject.skullOwner(null);
        itemObject.lore(new LinkedList<>());
        itemObject.id(0);
        return itemObject;
    }

    /**
     * Gets the lore of this item
     *
     * @return lore as list
     */
    List<String> getLore();

    /**
     * The sub-id of this item if needed
     *
     * @return short id
     */
    int getId();

    /**
     * The material as unknown enum
     *
     * @return material enum
     */
    String getMaterial();

    /**
     * The pre slot for inventory
     *
     * @return slot as int
     */
    int getPreInventorySlot();

    /**
     * The displayname of this item
     *
     * @return display name
     */
    String getDisplayName();

    /**
     * The amount of this item-stack
     * @return amount as int
     */
    int getAmount();

    /**
     * If this item is unbreakable
     *
     * @return boolean
     */
    boolean isUnbreakable();

    /**
     * If this item is glowing
     *
     * @return boolean
     */
    boolean isGlow();

    /**
     * The skull owner of this item
     * Returns null if this item is not a skull
     *
     * @return owner or null
     */
    String getSkullOwner();

    /**
     * Sets the material of this item
     *
     * @param material the material
     * @return current item
     */
    Item material(Enum<?> material);

    /**
     * Sets the material of this item
     *
     * @param material the material
     * @return current item
     */
    @Deprecated
    Item material(String material);

    /**
     * Sets the amount of this item
     *
     * @param amount the amount
     * @return current item
     */
    Item amount(int amount);

    /**
     * Sets a pre-slot for the inventory
     * its going to be put in
     *
     * @param slot the slot
     * @return current item
     */
    Item slot(int slot);

    /**
     * Sets the id of this item
     *
     * @param id the id
     * @return current item
     */
    Item id(int id);

    /**
     * Sets the displayname to a blank name
     *
     * @return current item
     */
    Item noName();

    /**
     * Makes the item glow
     *
     * @return current item
     */
    Item glow();

    /**
     * Makes the item unbreakable
     *
     * @return current item
     */
    Item unbreakable();

    /**
     * Sets the skullOwner of this item
     *
     * @param owner the skullOwner
     * @return current item
     */
    Item skullOwner(String owner);

    /**
     * Adds a single lore-line to the lore
     *
     * @param lore the line
     * @return current item
     */
    Item lore(String lore);

    /**
     * Sets the lore of this item
     *
     * @param lore the lore
     * @return current item
     */
    Item lore(List<String> lore);

    /**
     * Sets the displayName of this item
     *
     * @param name the displayName
     * @return current item
     */
    Item display(String name);
}
