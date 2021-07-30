package de.lystx.hytoracloud.driver.commons.packets.both.player;

import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.inventory.CloudPlayerInventory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;

@Getter @AllArgsConstructor
public class PacketInventoryUpdate extends PacketCommunication {

    private ICloudPlayer cloudPlayer;
    private CloudPlayerInventory playerInventory;


    @Override
    public void read(Component component) {
        super.read(component);

        cloudPlayer = component.get("cloudPlayer");
        playerInventory = component.get("inv");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
           map.put("cloudPlayer", cloudPlayer);
           map.put("inv", playerInventory);
        });
    }

}
