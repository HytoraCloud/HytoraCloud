package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.in;

import de.lystx.hytoracloud.driver.serverselector.sign.CloudSign;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@Getter @AllArgsConstructor
public class PacketInCloudSignCreate extends Packet {

    private CloudSign cloudSign;

    @Override
    public void write(Component component) {
        component.put("cloudSign", cloudSign);
    }

    @Override
    public void read(Component component) {
        cloudSign = component.get("cloudSign");
    }
}
