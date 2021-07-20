package de.lystx.hytoracloud.driver.commons.implementations;



import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.CloudItem;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.Inventory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter @NoArgsConstructor
public class InventoryObject implements Inventory {

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
    private final Map<Integer, CloudItem> items = new HashMap<>();

    public InventoryObject(String name, int rows) {
        this.title = name;
        this.rows = rows;
    }

    @Override
    public Inventory fillItem(CloudItem item) {
        for (int i = 0; i < this.rows * 9; i++) {
            this.setItem(i, item);
        }
        return this;
    }

    @Override
    public Inventory addItem(CloudItem item) {
        for (int i = 0; i < this.getSize(); i++) {
            if (this.getItem(i) == null) {
                this.setItem(i, item);
                break;
            }
        }
        return this;
    }

    @Override
    public InventoryObject setItem(int i, CloudItem item) {
        this.items.put(i, item);
        return this;
    }

    @Override
    public CloudItem getItem(int i) {
        return this.items.get(i);
    }

}
