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
    static Inventory create() {
        return new InventoryObject();
    }

    /**
     * Creates a new {@link Inventory} with a given title and rows
     *
     * @param rows the amount of rows
     * @param title the title
     * @return created inventory
     */
    static Inventory create(int rows, String title) {
        return new InventoryObject(title, rows);
    }

    /**
     * Sets the title of the inventory
     *
     * @param title the title
     */
    void setTitle(String title);

    /**
     * Gets the title of the inventory
     *
     * @return the title
     */
    String getTitle();

    /**
     * Sets the rows of this inventory
     *
     * @param rows the amount
     */
    void setRows(int rows);

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
    Inventory setItem(int slot, CloudItem item);

    /**
     * Adds an item to the first free slot
     *
     * @param item the item
     * @return current inventory
     */
    Inventory addItem(CloudItem item);

    /**
     * Fills the whole inventory with a given item
     *
     * @param item the item
     * @return current inventory
     */
    Inventory fillItem(CloudItem item);

    /**
     * Gets all items in this inventory
     *
     * @return the items
     */
    Map<Integer, CloudItem> getItems();

    /**
     * Gets an item at a given slot
     *
     * @param slot the slot
     * @return item or null
     */
    default CloudItem getItem(int slot) {
        return getItems().getOrDefault(slot, null);
    }
}
