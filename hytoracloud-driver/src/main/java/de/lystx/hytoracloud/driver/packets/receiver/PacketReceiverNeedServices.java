package de.lystx.hytoracloud.driver.packets.receiver;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.service.group.IServiceGroup;
import de.lystx.hytoracloud.driver.wrapped.GroupObject;
import de.lystx.hytoracloud.driver.wrapped.ReceiverObject;
import lombok.AllArgsConstructor;
import lombok.Getter;



@AllArgsConstructor @Getter
public class PacketReceiverNeedServices extends JsonPacket {


    @PacketSerializable(ReceiverObject.class)
    private IReceiver receiver;

    @PacketSerializable(GroupObject.class)
    private IServiceGroup serviceGroup;

}
