package de.lystx.hytoracloud.driver.commons.packets.both.player;

import de.lystx.hytoracloud.driver.commons.packets.both.PacketCommunication;
import de.lystx.hytoracloud.driver.commons.minecraft.chat.ChatComponent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import de.lystx.hytoracloud.networking.elements.component.Component;

import java.util.UUID;

@Getter  @AllArgsConstructor
public class PacketSendComponent extends PacketCommunication {

    private UUID uuid;
    private ChatComponent chatComponent;


    @Override
    public void read(Component component) {
        super.read(component);

        uuid = component.get("uuid");
        chatComponent = (ChatComponent) component.get("c");
    }

    @Override
    public void write(Component component) {
        super.write(component);

        component.append(map -> {
           map.put("uuid", uuid);
           map.put("c", chatComponent);
        });
    }

}
