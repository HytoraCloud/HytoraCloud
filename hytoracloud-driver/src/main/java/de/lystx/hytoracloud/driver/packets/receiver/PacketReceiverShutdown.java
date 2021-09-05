package de.lystx.hytoracloud.driver.packets.receiver;

import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.wrapped.ReceiverObject;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter @AllArgsConstructor
public class PacketReceiverShutdown extends JsonPacket {

    @PacketSerializable(ReceiverObject.class)
    private IReceiver receiver;

}
