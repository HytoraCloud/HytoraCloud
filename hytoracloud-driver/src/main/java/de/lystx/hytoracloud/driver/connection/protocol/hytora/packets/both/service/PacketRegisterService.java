package de.lystx.hytoracloud.driver.connection.protocol.hytora.packets.both.service;


import de.lystx.hytoracloud.driver.service.IService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.component.Component;
import de.lystx.hytoracloud.driver.connection.protocol.hytora.elements.packet.Packet;

@Getter @Setter @AllArgsConstructor
public class PacketRegisterService extends Packet {

    private String service;

    private IService iService;

    public PacketRegisterService(String service) {
        this(service, null);
    }

    @Override
    public void read(Component component) {

        service = component.get("service");
        iService = component.get("iService");
    }

    @Override
    public void write(Component component) {
        component.put("iService", iService);
        component.put("service", service);
    }

}
