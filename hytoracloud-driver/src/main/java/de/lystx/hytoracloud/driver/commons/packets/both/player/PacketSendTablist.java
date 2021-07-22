package de.lystx.hytoracloud.driver.commons.packets.both.player;

import de.lystx.hytoracloud.driver.commons.minecraft.chat.ChatComponent;
import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hytora.networking.elements.component.Component;

import java.util.UUID;

@Getter  @AllArgsConstructor
public class PacketSendTablist extends PacketCommunication {

    private UUID uuid;
    private ChatComponent header;
    private ChatComponent footer;


    @Override
    public void read(Component component) {
        super.read(component);

        uuid = component.get("uuid");
        header = component.get("header");
        footer = component.get("footer");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
           map.put("uuid", uuid);
           map.put("header", header);
           map.put("footer", footer);
        });
    }

}
