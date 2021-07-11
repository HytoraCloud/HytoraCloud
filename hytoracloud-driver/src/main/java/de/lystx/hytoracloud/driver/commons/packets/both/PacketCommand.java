package de.lystx.hytoracloud.driver.commons.packets.both;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketCommand extends PacketCommunication implements Serializable {

    private String service;
    private String command;


    @Override
    public void read(Component component) {
        super.read(component);

        service = component.get("s");
        command = component.get("c");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.put("s", service).put("c", command);
    }

}
