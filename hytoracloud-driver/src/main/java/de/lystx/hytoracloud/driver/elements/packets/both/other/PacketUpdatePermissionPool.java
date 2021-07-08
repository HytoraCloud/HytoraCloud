package de.lystx.hytoracloud.driver.elements.packets.both.other;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hytora.networking.elements.component.Component;

@NoArgsConstructor @Getter @AllArgsConstructor
public class PacketUpdatePermissionPool extends PacketCommunication {

    private PermissionPool permissionPool;


    @Override
    public void read(Component component) {
        super.read(component);

        permissionPool = (PermissionPool) component.getObject("pool");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> map.put("pool", permissionPool));
    }

}
