package de.lystx.hytoracloud.driver.packets.out;

import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.service.IService;


import de.lystx.hytoracloud.driver.wrapped.ServiceObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Getter @Setter @AllArgsConstructor
public class PacketOutRegisterServer extends JsonPacket {

    @PacketSerializable(ServiceObject.class)
    private IService service;
}
