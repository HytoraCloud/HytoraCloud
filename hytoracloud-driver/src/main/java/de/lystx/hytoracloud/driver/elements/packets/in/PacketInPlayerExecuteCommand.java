package de.lystx.hytoracloud.driver.elements.packets.in;



import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.IOException;
import java.io.Serializable;

/**
 * This packet just shows
 * that a player has executed a
 * command and you can get the whole
 * command line the player executed
 */
@Getter @AllArgsConstructor
public class PacketInPlayerExecuteCommand extends HytoraPacket implements Serializable {

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
