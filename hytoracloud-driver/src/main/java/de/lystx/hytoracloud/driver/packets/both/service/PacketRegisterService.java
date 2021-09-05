package de.lystx.hytoracloud.driver.packets.both.service;


import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.JsonPacket;
import de.lystx.hytoracloud.driver.connection.protocol.netty.global.packet.impl.json.PacketSerializable;
import de.lystx.hytoracloud.driver.service.IService;
import de.lystx.hytoracloud.driver.wrapped.ServiceObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Getter @Setter @AllArgsConstructor
public class PacketRegisterService extends JsonPacket {

    @PacketSerializable
    private String service;

    @PacketSerializable(ServiceObject.class)
    private IService iService;

    public PacketRegisterService(String service) {
        this(service, null);
    }

}
