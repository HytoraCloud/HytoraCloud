package de.lystx.hytoracloud.driver.elements.packets.in;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
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
    public void read(PacketBuffer buf) {
        player = buf.readString();
        command = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(player);
        buf.writeString(command);
    }
}
