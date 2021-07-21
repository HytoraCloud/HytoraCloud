package de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.packets.both.player.PacketInventoryUpdate;
import de.lystx.hytoracloud.driver.CloudDriver;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class CloudPlayerInventory implements Serializable {

    private static final long serialVersionUID = 7903524609725996284L;
    private CloudItem helmet, chestplate, leggings, boots;
    private Map<Integer, CloudItem> slots;

    private final ICloudPlayer cloudPlayer;

    public CloudPlayerInventory(ICloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
        this.slots = new HashMap<>();
    }

    /**
     * Sets a {@link CloudItem} to a
     * slot in the {@link CloudPlayerInventory}
     * @param i
     * @param item
     */
    public void setItem(int i, CloudItem item) {
        this.slots.put(i, item);
    }

    /**
     * Updates the inventory
     * without clearing
     */
    public void update() {
        this.update(false);
    }

    /**
     * Updates the {@link CloudPlayerInventory} of
     * the given CloudPlayer
     * @param clearAfter > If true it will set all
     *               values to null and clear the items
     */
    public void update(boolean clearAfter) {
        CloudDriver.getInstance().getConnection().sendPacket(new PacketInventoryUpdate(this.cloudPlayer, this));
        if (clearAfter) {
            this.slots = new HashMap<>();
            this.helmet = null;
            this.chestplate = null;
            this.leggings = null;
            this.boots = null;
        }
    }

}
