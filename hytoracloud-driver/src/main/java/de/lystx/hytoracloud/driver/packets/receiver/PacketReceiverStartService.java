package de.lystx.hytoracloud.driver.packets.receiver;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.service.receiver.IReceiver;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.wrapped.ReceiverObject;
import de.lystx.hytoracloud.driver.wrapped.ServiceObject;
import lombok.AllArgsConstructor;
import lombok.Getter;



@AllArgsConstructor @Getter
public class PacketReceiverStartService extends JsonPacket {


    @PacketSerializable(ReceiverObject.class)
    private IReceiver receiver;


    @PacketSerializable(ServiceObject.class)
    private IService service;

}
