package de.lystx.hytoracloud.driver.commons.packets.both.player;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.commons.implementations.InventoryObject;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketOpenInventory extends PacketCommunication implements Serializable {

    private ICloudPlayer ICloudPlayer;
    private InventoryObject inventoryObject;

    @Override
    public void read(Component component) {
        super.read(component);

        ICloudPlayer = (ICloudPlayer) component.get("cloudPlayer");
        inventoryObject = (InventoryObject) component.get("inv");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
            map.put("cloudPlayer", ICloudPlayer);
            map.put("inv", inventoryObject);
        });
    }

}
