package de.lystx.hytoracloud.driver.commons.packets.out.receiver;

import de.lystx.hytoracloud.driver.utils.utillity.ReceiverInfo;
import de.lystx.hytoracloud.driver.commons.service.ServiceGroup;
import de.lystx.hytoracloud.driver.commons.enums.other.Decision;


import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;
import net.hytora.networking.elements.packet.HytoraPacket;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor @Getter
public class PacketReceiverLoginResult extends HytoraPacket implements Serializable {

    private ReceiverInfo receiverInfo;
    private Decision decision;
    private List<ServiceGroup> serviceGroups;

    @Override
    public void write(Component component) {
        component.put("r", receiverInfo).put("d", decision.name()).put("s", serviceGroups);
    }

    @Override
    public void read(Component component) {

        receiverInfo = component.get("r");
        decision = Decision.valueOf(component.get("d"));
        serviceGroups = component.get("s");
    }
}
