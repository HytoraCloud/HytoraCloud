package de.lystx.hytoracloud.driver.commons.packets.in;

import de.lystx.hytoracloud.driver.cloudservices.managing.serverselector.sign.base.CloudSign;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

@Getter @AllArgsConstructor
public class PacketInCloudSignDelete extends HytoraPacket {

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
