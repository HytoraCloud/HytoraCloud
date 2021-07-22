package de.lystx.hytoracloud.driver.commons.packets.both.player;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.commons.wrapped.InventoryObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

@Getter @AllArgsConstructor
public class PacketOpenInventory extends PacketCommunication  {

    private de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer cloudPlayer;
    private InventoryObject inventoryObject;

    @Override
    public void read(Component component) {
        super.read(component);

        cloudPlayer = component.get("cloudPlayer");
        inventoryObject = component.get("inv");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
            map.put("cloudPlayer", cloudPlayer);
            map.put("inv", inventoryObject);
        });
    }

}
