package de.lystx.hytoracloud.driver.elements.packets.both.player;


import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

@Getter  @AllArgsConstructor
public class PacketPlaySound extends PacketCommunication {

    private String name;
    private String sound;
    private float v1;
    private float v2;

    @Override
    public void read(Component component) {
        super.read(component);

        name = component.get("name");
        sound = component.get("sound");
        v1 = Float.parseFloat(component.get("v1"));
        v2 = Float.parseFloat(component.get("v2"));
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
           map.put("name", name);
           map.put("sound", sound);
           map.put("v1", String.valueOf(v1));
           map.put("v2", String.valueOf(v2));
        });
    }

}
