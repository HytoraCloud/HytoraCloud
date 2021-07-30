package de.lystx.hytoracloud.driver.commons.packets.in;



import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

import java.io.Serializable;

/**
 * This packet just shows
 * that a player has executed a
 * command and you can get the whole
 * command line the player executed
 */
@Getter @AllArgsConstructor
public class PacketInPlayerExecuteCommand extends Packet implements Serializable {

    private String player;
    private String command;

    @Override
    public void write(Component component) {
        component.put("p", player).put("c", command);
    }

    @Override
    public void read(Component component) {
        player = component.get("p");
        command = component.get("c");
    }
}
