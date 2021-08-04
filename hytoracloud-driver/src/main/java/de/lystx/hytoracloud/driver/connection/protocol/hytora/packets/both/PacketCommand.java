package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketCommand extends Packet {

    private String service;
    private String command;


    @Override
    public void read(Component component) {

        service = component.get("s");
        command = component.get("c");
    }

    @Override
    public void write(Component component) {

        component.put("s", service).put("c", command);
    }

}
