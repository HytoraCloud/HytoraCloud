package de.lystx.hytoracloud.driver.elements.packets.both.other;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.elements.service.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.io.Serializable;

@Getter @AllArgsConstructor
public class PacketTPS extends PacketCommunication implements Serializable {

    private String player;
    private Service service;
    private String tps;


    @Override
    public void read(Component component) {
        super.read(component);

        player = component.getString("player");
        service = (Service) component.getObject("service");
        tps = component.getString("tps");
    }


    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
           map.put("player", player);
           map.put("service", service);
           map.put("tps", tps);
        });
    }

}
