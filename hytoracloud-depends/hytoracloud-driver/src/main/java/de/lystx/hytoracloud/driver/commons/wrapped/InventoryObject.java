package de.lystx.hytoracloud.driver.commons.wrapped;


import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.Inventory;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter @NoArgsConstructor
public class InventoryObject extends WrappedObject<Inventory, InventoryObject> implements Inventory {

    private static final long serialVersionUID = -1491563633773945463L;

    /**
     * The title of the inventory
     */
    private String title;

    /**
     * The rows of the inventory
     */
    private int rows;

    /**
     * The cached items
     */
    private Map<Integer, Item> items;

    public InventoryObject(String name, int rows) {
        this.title = name;
        this.rows = rows;
    }

    @Override
    public Inventory fill(Item item) {
        for (int i = 0; i < this.rows * 9; i++) {
            this.set(i, item);
        }
        return this;
    }

    @Override
    public Inventory corners(Item item) {

        int size = this.getSize();
        int rows = (size + 1) / 9;

        // Fill top
        for (int i = 0; i < 9; i++) {
            this.set(i, item);
        }

        // Fill bottom
        for (int i = size - 9; i < size; i++) {
            this.set(i, item);
        }

        // Fill sides
        for (int i = 2; i <= rows - 1; i++) {
            int[] slots = new int[]{i * 9 - 1, (i - 1) * 9};
            this.set(slots[0], item);
            this.set(slots[1], item);
        }

        return this;
    }

    @Override
    public Inventory add(Item item) {
        for (int i = 0; i < this.getSize(); i++) {
            if (this.getItem(i) == null) {
                this.set(i, item);
                break;
            }
        }
        return this;
    }

    @Override
    public InventoryObject set(int i, Item item) {
        this.items.put(i, item);
        return this;
    }


    @Override
    public Inventory info(String title, int rows) {
        this.rows = rows;
        this.title = title;
        this.items = new HashMap<>(rows * 9);
        return this;
    }

    @Override
    public Item getItem(int i) {
        return this.items.get(i);
    }

    @Override
    public Class<InventoryObject> getWrapperClass() {
        return InventoryObject.class;
    }

    @Override
    Class<Inventory> getInterface() {
        return Inventory.class;
    }
}
