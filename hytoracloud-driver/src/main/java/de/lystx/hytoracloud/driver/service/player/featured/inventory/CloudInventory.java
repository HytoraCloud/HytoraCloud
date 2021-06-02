package de.lystx.hytoracloud.driver.service.player.featured.inventory;

import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class CloudInventory implements Serializable, ThunderObject {

    private String name;
    private int rows;
    private Map<Integer, CloudItem> items;

    /**
     * Creates the Inventory
     * @param name
     * @param rows
     */
    public CloudInventory(String name, int rows) {
        this.name = name;
        this.rows = rows;
        this.items = new HashMap<>();
    }

    /**
     * Fills it with a {@link CloudItem}
     * @param item
     * @return
     */
    public CloudInventory fill(CloudItem item) {
        for (int i = 0; i < this.rows * 9; i++) {
            this.setItem(i, item);
        }
        return this;
    }

    /**
     * Sets a {@link CloudItem} at a specific
     * slot in the {@link CloudInventory}
     * @param i
     * @param item
     * @return
     */
    public CloudInventory setItem(int i, CloudItem item) {
        this.items.put(i, item);
        return this;
    }

    /**
     * Returns CloudItem at a
     * given Slot
     * @param i
     * @return
     */
    public CloudItem getItem(int i) {
        return this.items.get(i);
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeInt(rows);
        buf.writeString(name);

        buf.writeInt(items.size());
        for (Integer integer : items.keySet()) {
            buf.writeInt(integer);
            buf.writeThunderObject(items.get(integer));
        }
    }

    @Override
    public void read(PacketBuffer buf) {

        rows = buf.readInt();
        name = buf.readString();

        int size = buf.readInt();
        items = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            items.put(buf.readInt(), buf.readThunderObject(CloudItem.class));
        }
    }
}
