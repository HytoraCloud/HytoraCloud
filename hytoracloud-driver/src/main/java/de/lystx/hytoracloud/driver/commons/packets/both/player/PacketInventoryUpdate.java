package de.lystx.hytoracloud.driver.commons.packets.both.player;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.featured.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.cloudservices.managing.player.impl.ICloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInventoryUpdate extends PacketCommunication implements Serializable {

    private ICloudPlayer ICloudPlayer;
    private CloudPlayerInventory playerInventory;


    @Override
    public void read(Component component) {
        super.read(component);

        ICloudPlayer = (ICloudPlayer) component.get("cloudPlayer");
        playerInventory = (CloudPlayerInventory) component.get("inv");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
           map.put("cloudPlayer", ICloudPlayer);
           map.put("inv", playerInventory);
        });
    }

}
