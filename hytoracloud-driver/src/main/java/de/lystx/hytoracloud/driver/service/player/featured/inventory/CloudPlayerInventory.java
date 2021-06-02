package de.lystx.hytoracloud.driver.service.player.featured.inventory;

import de.lystx.hytoracloud.driver.elements.packets.both.player.PacketInventoryUpdate;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import de.lystx.hytoracloud.driver.CloudDriver;
import io.thunder.packet.PacketBuffer;
import io.thunder.utils.objects.ThunderObject;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class CloudPlayerInventory implements Serializable, ThunderObject {

    private CloudItem helmet, chestplate, leggings, boots;
    private Map<Integer, CloudItem> slots;

    private final CloudPlayer cloudPlayer;

    public CloudPlayerInventory(CloudPlayer cloudPlayer) {
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
        CloudDriver.getInstance().getCloudInventories().put(this.cloudPlayer.getUniqueId(), this);
        CloudDriver.getInstance().getConnection().sendPacket(new PacketInventoryUpdate(this.cloudPlayer, this));
        if (clearAfter) {
            this.slots = new HashMap<>();
            this.helmet = null;
            this.chestplate = null;
            this.leggings = null;
            this.boots = null;
        }
    }

    @Override
    public void write(PacketBuffer buf) {

        buf.writeThunderObject(helmet);
        buf.writeThunderObject(chestplate);
        buf.writeThunderObject(leggings);
        buf.writeThunderObject(boots);

        buf.writeInt(slots.size());
        for (Integer integer : slots.keySet()) {
            buf.writeInt(integer);
            buf.writeThunderObject(slots.get(integer));
        }

    }

    @Override
    public void read(PacketBuffer buf) {

        helmet = buf.readThunderObject(CloudItem.class);
        chestplate = buf.readThunderObject(CloudItem.class);
        leggings = buf.readThunderObject(CloudItem.class);
        boots = buf.readThunderObject(CloudItem.class);

        int size = buf.readInt();
        slots = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            slots.put(buf.readInt(), buf.readThunderObject(CloudItem.class));
        }
    }
}
