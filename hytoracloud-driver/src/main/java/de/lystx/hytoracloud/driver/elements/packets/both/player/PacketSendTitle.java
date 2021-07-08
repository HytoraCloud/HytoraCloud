package de.lystx.hytoracloud.driver.elements.packets.both.player;

import de.lystx.hytoracloud.driver.elements.packets.both.PacketCommunication;
import io.thunder.packet.PacketBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

@Getter @AllArgsConstructor
public class PacketSendTitle extends PacketCommunication {

    private String name;
    private String title;
    private String subtitle;


    @Override
    public void read(Component component) {
        super.read(component);

        name = component.getString("name");
        title = component.getString("title");
        subtitle = component.getString("subtitle");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
            map.put("name", name);
            map.put("title", title);
            map.put("subtitle", subtitle);
        });
    }

}
