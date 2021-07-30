package de.lystx.hytoracloud.driver.commons.packets.in.request.perms;

import de.lystx.hytoracloud.driver.cloudservices.managing.permission.impl.PermissionValidity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

import java.util.UUID;


@AllArgsConstructor @Getter
public class PacketRequestPermissionGroupAdd extends Packet {

    private UUID playerUUID;
    private String group;
    private int i;
    private PermissionValidity validality;

    @Override
    public void write(Component component) {
        component.put("uuid", playerUUID);
        component.put("group", group);
        component.put("i", i);
        component.put("valid", validality);
    }

    @Override
    public void read(Component component) {
        playerUUID = component.get("uuid");
        group = component.get("group");
        i = component.get("i");
        validality = component.get("valid");
    }
}
