package de.lystx.hytoracloud.driver.elements.packets.in;

import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.io.Serializable;

/**
 * This packet is used to handle
 * the log of a service
 * it will upload it to a pasteserver
 * and return the link of it
 */
@Getter @AllArgsConstructor
public class PacketInGetLog extends Packet implements Serializable {

    private String service;
    private String player;

    @Override
    public void read(PacketBuffer buf) {
        service = buf.readString();
        player = buf.readString();
    }

    @Override
    public void write(PacketBuffer buf) {
        buf.writeString(service);
        buf.writeString(player);
    }
}
