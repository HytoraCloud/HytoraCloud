package de.lystx.cloudsystem.library.elements.packets.communication;

import de.lystx.cloudsystem.library.elements.chat.CloudComponent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PacketCommunicationSendComponent extends PacketCommunication {

    private final UUID uuid;
    private final CloudComponent cloudComponent;

    public PacketCommunicationSendComponent(UUID uuid, CloudComponent cloudComponent) {
        super(PacketCommunicationSendComponent.class);
        this.uuid = uuid;
        this.cloudComponent = cloudComponent;
    }
}
