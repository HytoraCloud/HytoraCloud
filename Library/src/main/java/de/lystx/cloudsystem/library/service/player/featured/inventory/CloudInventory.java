package de.lystx.cloudsystem.library.service.player.featured.inventory;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CloudInventory implements Serializable {

    private final String name;
    private final int rows;
    private final Map<Integer, CloudItem> items;

    public CloudInventory(String name, int rows) {
        this.name = name;
        this.rows = rows;
        this.items = new HashMap<>();
    }

    public CloudInventory fill(CloudItem item) {
        for (int i = 0; i < this.rows * 9; i++) {
            this.setItem(i, item);
        }
        return this;
    }

    public CloudInventory setItem(int i, CloudItem item) {
        this.items.put(i, item);
        return this;
    }

    public CloudItem getItem(int i) {
        return this.items.get(i);
    }
}
