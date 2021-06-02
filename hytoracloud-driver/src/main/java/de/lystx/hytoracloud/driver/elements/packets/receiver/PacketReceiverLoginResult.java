package de.lystx.hytoracloud.driver.elements.packets.receiver;

import de.lystx.hytoracloud.driver.elements.other.ReceiverInfo;
import de.lystx.hytoracloud.driver.elements.service.ServiceGroup;
import de.lystx.hytoracloud.driver.enums.Decision;
import io.thunder.packet.Packet;
import io.thunder.packet.PacketBuffer;
import io.vson.elements.object.VsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor @Getter
public class PacketReceiverLoginResult extends Packet implements Serializable {

    private ReceiverInfo receiverInfo;
    private Decision decision;
    private List<ServiceGroup> serviceGroups;

    @Override @SneakyThrows
    public void read(PacketBuffer buf) {
        receiverInfo = ReceiverInfo.fromBuf(buf);
        decision = buf.readEnum(Decision.class);
        serviceGroups = new VsonObject(buf.readString()).getAs(List.class);
    }

    @Override
    public void write(PacketBuffer buf) {
        receiverInfo.toBuf(buf);
        buf.writeEnum(decision);
        buf.writeString(new VsonObject().append(serviceGroups).toString());
    }
}
