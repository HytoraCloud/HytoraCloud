package de.lystx.cloudsystem.library.service.player.featured;

import de.lystx.cloudsystem.library.elements.packets.both.PacketInventoryUpdate;
import de.lystx.cloudsystem.library.service.player.impl.CloudPlayer;
import de.lystx.cloudsystem.library.service.util.Constants;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter() @Setter
public class CloudPlayerInventory implements Serializable {

    private CloudItem helmet, chestplate, leggings, boots;
    private Map<Integer, CloudItem> slots;

    private final CloudPlayer cloudPlayer;

    public CloudPlayerInventory(CloudPlayer cloudPlayer) {
        this.cloudPlayer = cloudPlayer;
        this.slots = new HashMap<>();
    }

    public void setItem(int i, CloudItem item) {
        this.slots.put(i, item);
    }

    public void update() {
        this.update(false);
    }

    public void update(boolean clearAfter) {
        Constants.INVENTORIES.put(this.cloudPlayer.getUniqueId(), this);
        new PacketInventoryUpdate(this.cloudPlayer, this).unsafe().async().send();
        if (clearAfter) {
            this.slots = new HashMap<>();
            this.helmet = null;
            this.chestplate = null;
            this.leggings = null;
            this.boots = null;
        }
    }
}
