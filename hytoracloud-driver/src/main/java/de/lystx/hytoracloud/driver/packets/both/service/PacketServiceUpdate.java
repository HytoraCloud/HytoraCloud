package de.lystx.hytoracloud.driver.packets.both.service;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.forwarding.ForwardingPacketJson;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.service.IService;

import de.lystx.hytoracloud.driver.wrapped.ServiceObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketServiceUpdate extends ForwardingPacketJson {

    @PacketSerializable(ServiceObject.class)
    private IService service;

}
