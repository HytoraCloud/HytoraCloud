package de.lystx.hytoracloud.driver.commons.packets.both.service;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.util.UUID;

@Getter @AllArgsConstructor
public class PacketConnectServer extends PacketCommunication {

    private UUID uuid;
    private String server;

    @Override
    public void read(Component component) {
        super.read(component);

        uuid = component.get("uuid");
        server = component.get("server");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
            map.put("uuid", uuid);
            map.put("server", server);
        });
    }

}
