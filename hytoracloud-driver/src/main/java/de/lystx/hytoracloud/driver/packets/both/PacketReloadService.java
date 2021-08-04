package de.lystx.hytoracloud.driver.packets.both;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.wrapped.ServiceObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PacketReloadService extends JsonPacket {

    @PacketSerializable(ServiceObject.class)
    private IService service;

}

