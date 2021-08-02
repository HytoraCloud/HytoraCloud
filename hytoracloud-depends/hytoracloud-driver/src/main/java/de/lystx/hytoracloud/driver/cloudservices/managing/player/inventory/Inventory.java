package de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory;

import de.lystx.hytoracloud.driver.commons.wrapped.InventoryObject;

import java.io.Serializable;
import java.util.Map;

public interface Inventory extends Serializable {

    /**
     * Creates a new {@link Inventory} with no name and rows
     *
     * @return created inventory
     */
    static Inventory builder() {
        return new InventoryObject();
    }

    /**
     * Sets the title of the inventory
     *
     * @param title the title
     */
    Inventory info(String title, int rows);

    /**
     * Gets the title of the inventory
     *
     * @return the title
     */
    String getTitle();

    /**
     * Gets the rows of the inventory
     *
     * @return the rows (!= size)
     */
    int getRows();

    /**
     * Gets the size of this inventory
     *
     * @return the size
     */
    default int getSize() {
        return getRows() * 9;
    }

    /**
     * Sets an item to a given slot
     *
     * @param slot the slot
     * @param item the item
     * @return current inventory
     */
    Inventory set(int slot, Item item);

    /**
     * Adds an item to the first free slot
     *
     * @param item the item
     * @return current inventory
     */
    Inventory add(Item item);

    /**
     * Fills the whole inventory with a given item
     *
     * @param item the item
     * @return current inventory
     */
    Inventory fill(Item item);

    /**
     * Fills the corners of inventory with a given item
     *
     * @param item the item
     * @return current inventory
     */
    Inventory corners(Item item);

    /**
     * Gets all items in this inventory
     *
     * @return the items
     */
    Map<Integer, Item> getItems();

    /**
     * Gets an item at a given slot
     *
     * @param slot the slot
     * @return item or null
     */
    default Item getItem(int slot) {
        return getItems().getOrDefault(slot, null);
    }
}
