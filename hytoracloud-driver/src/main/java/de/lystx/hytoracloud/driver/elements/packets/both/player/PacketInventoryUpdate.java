package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.service.player.featured.inventory.CloudPlayerInventory;
import de.lystx.hytoracloud.driver.service.player.impl.CloudPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketInventoryUpdate extends PacketCommunication implements Serializable {

    private CloudPlayer cloudPlayer;
    private CloudPlayerInventory playerInventory;


    @Override
    public void read(Component component) {
        super.read(component);

        cloudPlayer = (CloudPlayer) component.getObject("cloudPlayer");
        playerInventory = (CloudPlayerInventory) component.getObject("inv");
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
