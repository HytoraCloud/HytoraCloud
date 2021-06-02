package de.lystx.hytoracloud.driver.elements.packets.both.other;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import de.lystx.hytoracloud.driver.service.permission.impl.PermissionPool;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor @Getter @AllArgsConstructor
public class PacketUpdatePermissionPool extends PacketCommunication {

    private PermissionPool permissionPool;

    @Override
    public void read(PacketBuffer buf) {
        super.read(buf);

        permissionPool = buf.readThunderObject(PermissionPool.class);
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);

        buf.writeThunderObject(permissionPool);
    }

}
