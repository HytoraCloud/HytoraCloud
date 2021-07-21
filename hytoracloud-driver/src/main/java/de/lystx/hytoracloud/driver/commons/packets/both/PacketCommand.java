package de.lystx.hytoracloud.driver.commons.packets.both;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketCommand extends HytoraPacket {

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
