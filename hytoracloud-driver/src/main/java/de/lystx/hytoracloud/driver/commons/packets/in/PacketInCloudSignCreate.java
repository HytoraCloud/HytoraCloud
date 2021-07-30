package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.CloudSign;
import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;
import de.lystx.hytoracloud.networking.elements.packet.Packet;

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
