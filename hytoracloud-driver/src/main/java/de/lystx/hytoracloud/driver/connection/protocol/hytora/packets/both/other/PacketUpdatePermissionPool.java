package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.other;

import de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.player.permission.impl.PermissionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;

@NoArgsConstructor @Getter @AllArgsConstructor
public class PacketUpdatePermissionPool extends PacketCommunication {

    private PermissionPool permissionPool;


    @Override
    public void read(Component component) {
        super.read(component);

        permissionPool = component.get("pool");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> map.put("pool", permissionPool));
    }

}
